from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import psycopg2
import numpy as np
from datetime import timedelta

app = FastAPI()


app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"], 
    allow_headers=["*"], 
)


class PesoPrediccionRequest(BaseModel):
    animal_id: int
    meses: int

class ConsumoPrediccionRequest(BaseModel):
    corral_id: int
    dias: int

class PesoPrediccion(BaseModel):
    fecha: str
    pesoKg: float
    predicho: bool

class ConsumoPrediccion(BaseModel):
    fecha: str
    consumoKg: float
    predicho: bool


def get_connection():
    return psycopg2.connect(
        host="localhost",
        port=5433,
        database="feedlotappdb",
        user="feedlotuser",
        password="Kennys360"
    )

# peso
@app.post("/predict/peso", response_model=list[PesoPrediccion])
def predecir_peso(req: PesoPrediccionRequest):

    conn = get_connection()
    cur = conn.cursor()

    cur.execute("""
        SELECT fecha, peso
        FROM pesaje
        WHERE animal_id = %s
        ORDER BY fecha
    """, (req.animal_id,))

    rows = cur.fetchall()
    cur.close()
    conn.close()

    if len(rows) < 2:
        raise HTTPException(400, "Se necesitan al menos 2 pesajes")

    fechas = [r[0] for r in rows]
    pesos = np.array([float(r[1]) for r in rows])

    base = fechas[0]
    dias = np.array([(f - base).days for f in fechas])

    coef = np.polyfit(dias, pesos, 1)

    resultado = []

    for f, p in zip(fechas, pesos):
        resultado.append({
            "fecha": f.isoformat(),
            "pesoKg": round(float(p), 2),
            "predicho": False
        })

    ultimo_dia = dias[-1]
    ultima_fecha = fechas[-1]

    for i in range(1, req.meses + 1):
        peso_pred = np.polyval(coef, ultimo_dia + (30 * i))
        resultado.append({
            "fecha": (ultima_fecha + timedelta(days=30 * i)).isoformat(),
            "pesoKg": round(float(peso_pred), 2),
            "predicho": True
        })

    return resultado

# consumo
@app.post("/predict/consumo", response_model=list[ConsumoPrediccion])
def predecir_consumo(req: ConsumoPrediccionRequest):

    conn = get_connection()
    cur = conn.cursor()

    cur.execute("""
        SELECT rc.fecha, SUM(rc.cantidad) AS total_consumo
    FROM registro_comedero rc
    WHERE rc.corral_id = %s
    GROUP BY rc.fecha
    ORDER BY rc.fecha

    """, (req.corral_id,))

    rows = cur.fetchall()
    cur.close()
    conn.close()

    if len(rows) < 3:
        return []

    fechas = [r[0] for r in rows]
    consumos = np.array([float(r[1]) for r in rows])

    base = fechas[0]
    dias_hist = np.array([(f - base).days for f in fechas])

    coef = np.polyfit(dias_hist, consumos, 1)

    resultado = []

    for f, c in zip(fechas, consumos):
        resultado.append({
            "fecha": f.isoformat(),
            "consumoKg": round(float(c), 2),
            "predicho": False
        })

    ultimo_dia = dias_hist[-1]
    ultima_fecha = fechas[-1]

    for i in range(1, req.dias + 1):
        pred = np.polyval(coef, ultimo_dia + i)
        resultado.append({
            "fecha": (ultima_fecha + timedelta(days=i)).isoformat(),
            "consumoKg": round(float(pred), 2),
            "predicho": True
        })

    return resultado
