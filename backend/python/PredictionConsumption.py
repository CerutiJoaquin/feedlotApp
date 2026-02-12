import psycopg2
import numpy as np
from datetime import timedelta


def get_connection():
    return psycopg2.connect(
        host="localhost",
        port=5433,
        database="feedlotappdb",
        user="feedlotuser",
        password="Kennys360"
    )


def predecir_consumo_corral(corral_id: int, dias_futuros: int):
    conn = get_connection()
    cur = conn.cursor()

    cur.execute("""
        SELECT fecha, SUM(cantidad)
        FROM registro_comedero rc
        JOIN insumo i ON rc.insumo_id = i.insumo_id
        WHERE rc.corral_id = %s
          AND i.tipo = 'ALIMENTO'
        GROUP BY fecha
        ORDER BY fecha
    """, (corral_id,))

    rows = cur.fetchall()
    cur.close()
    conn.close()

    if len(rows) < 5:
        return []

    fechas = [r[0] for r in rows]
    consumos = np.array([float(r[1]) for r in rows])

    fecha_base = fechas[0]
    dias_hist = np.array([(f - fecha_base).days for f in fechas])

    coef = np.polyfit(dias_hist, consumos, 1)

    resultado = []

    # ---- Datos reales ----
    for f, c in zip(fechas, consumos):
        resultado.append({
            "fecha": f.isoformat(),
            "consumoTotalKg": round(float(c), 2),
            "predicho": False
        })

    ultimo_dia = dias_hist[-1]
    ultima_fecha = fechas[-1]

    # ---- Predicción futura ----
    for i in range(1, dias_futuros + 1):
        consumo_pred = np.polyval(coef, ultimo_dia + i)

        resultado.append({
            "fecha": (ultima_fecha + timedelta(days=i)).isoformat(),
            "consumoTotalKg": round(float(consumo_pred), 2),
            "predicho": True
        })

    return resultado
