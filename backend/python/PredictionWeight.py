from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import psycopg2
import numpy as np
from datetime import timedelta

app = FastAPI()

# cors
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Request / Response DTOs
class PrediccionRequest(BaseModel):
    animal_id: int
    meses: int

class PesoPrediccion(BaseModel):
    fecha: str
    pesoKg: float
    predicho: bool

# DB Connection
def get_connection():
    return psycopg2.connect(
        host="localhost",
        port=5433,
        database="feedlotappdb",
        user="feedlotuser",
        password="Kennys360"
    )

# Endpoint
@app.post("/predict/peso", response_model=list[PesoPrediccion])
def predecir_peso(req: PrediccionRequest):

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
        raise HTTPException(
            status_code=400,
            detail="Se necesitan al menos 2 pesajes"
        )

    fechas = [r[0] for r in rows]
    pesos = np.array([float(r[1]) for r in rows])

    fecha_base = fechas[0]
    dias = np.array([(f - fecha_base).days for f in fechas])

    coef = np.polyfit(dias, pesos, 1)

    resultado = []

    # Pesos reales
    for f, p in zip(fechas, pesos):
        resultado.append({
            "fecha": f.isoformat(),
            "pesoKg": round(float(p), 2),
            "predicho": False
        })

    ultima_fecha = fechas[-1]
    ultimo_dia = (ultima_fecha - fecha_base).days

    # Predicción futura
    for i in range(1, req.meses + 1):
        dias_fut = ultimo_dia + (30 * i)
        peso_pred = np.polyval(coef, dias_fut)

        resultado.append({
            "fecha": (ultima_fecha + timedelta(days=30 * i)).isoformat(),
            "pesoKg": round(float(peso_pred), 2),
            "predicho": True
        })

    return resultado
