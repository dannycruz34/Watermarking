import plotly
from plotly import __version__
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot
from plotly.graph_objs import Scatter, Figure, Layout
import plotly.plotly as py
import plotly.graph_objs as go

#--------------------------------------------------------------------------------            
def graficar(datos_original, datos_modificado):
    x_orig=datos_original[0]
    y_orig=datos_original[1]
    y_mod=datos_modificado[1]
    
    trace0 = go.Scatter(
    x = x_orig,
    y = y_orig,
    name = 'Pseudoaleatorios LFSR mod l',
    line = dict(
        #color = ('rgb(205, 12, 24)'),
        width = 1),
        mode = 'lines+markers'
    )   
    trace1 = go.Scatter(
    x = x_orig,
    y = y_mod,
    name = 'HASH mod l',
    line = dict(
        #color = ('rgb(205, 12, 24)'),
        width = 1),
        mode = 'lines+markers'
    )
    data = [trace0, trace1]
            
    name = "Pseudoaleatorios LFSR vs HASH"
    # Edit the layout
    layout = dict(title = name,
        xaxis = dict(title = 'Number of Instances'),
        yaxis = dict(title = 'Bit Index (Seconds)'),
        )
    
    fig = dict(data=data, layout=layout)
    name = name + ".html"
    plot(fig, filename=name)

x=[]
y=[]
ym=[]

x.append("1")


y.append(1.223)
y.append(3.662)
y.append(7.912)
y.append(14.58)
y.append(22.001)
y.append(31.415)
y.append(47.327)
y.append(61.918)
y.append(76.553)
y.append(88.447)
y.append(204.356)
y.append(384.758)

ym.append(0.012)
ym.append(0.045)
ym.append(0.047)
ym.append(0.169)
ym.append(0.265)
ym.append(0.377)
ym.append(0.264)
ym.append(0.354)
ym.append(0.421)
ym.append(1.043)
ym.append(2.308)
ym.append(4.058)


datos1 =[]
datos1.append(x)
datos1.append(y)
datos2 =[]
datos2.append(x)
datos2.append(ym)

graficar(datos1,datos2)

