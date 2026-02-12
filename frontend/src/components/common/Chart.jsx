import React, { useEffect, useRef } from 'react'
import ChartJS from 'chart.js/auto'

export default function Chart({ tipo = 'line', data, dataKey, label }) {
  const canvasRef = useRef(null)

  
  
  
  useEffect(() => {
  const ctx = canvasRef.current.getContext('2d')


    const gradient = ctx.createLinearGradient(0, 0, 0, 200)
    gradient.addColorStop(0, 'rgba(76,175,80,0.3)')
    gradient.addColorStop(1, 'rgba(76,175,80,0)')

    const chart = new ChartJS(ctx, {
      type: tipo,
      data: {
        labels: data.map(d => d.mes),
        datasets: [{
          label: label,
          data: data.map(d => d[dataKey]),
          fill: true,                 
          backgroundColor: gradient,  
          borderColor: 'rgba(76,175,80,1)',
          borderWidth: 3,
          tension: 0.4,          
          pointRadius: 5,
          pointBackgroundColor: 'white',
          pointBorderColor: 'rgba(76,175,80,1)',
          pointBorderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              boxWidth: 12,
              padding: 10,
              color: '#333',
              font: { size: 12 }
            }
          }
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: '#666' }
          },
          y: {
            grid: { color: '#eee' },
            ticks: { color: '#666' }
          }
        }
      }
    })

    return () => chart.destroy()
  }, [data, dataKey, label, tipo])

  return (
    <div style={{ position: 'relative', width: '100%', height: '350px' }}>
      <canvas ref={canvasRef} />
    </div>
  )
}
