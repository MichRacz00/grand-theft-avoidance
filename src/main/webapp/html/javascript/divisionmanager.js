import React,{useState} from "react";
import MapGL from "react-map-gl"


function App(){

    const [viewport, setViewport] = useState({
        latitude: 45.4211,
        longitude: -75.6903,
        width: "100vw",
        height: "100vh",
        zoom: 10
    });

    return (
            <MapGL
                {...viewport}
                mapboxApiAccessToken= {"pk.eyJ1IjoidmFsZXJpYXZldmVyaXRhIiwiYSI6ImNrcGYzb25pbzA4NGoyb24wN3A5amt1dTIifQ.0eAA6SoRqW9QDJLkcYB-YA"}
                mapStyle="mapbox://styles/leighhalliday/cjufmjn1r2kic1fl9wxg7u1l4"
                onViewportChange={viewport => {
                    setViewport(viewport);
                }}
            />
    );
}

ReactDOM.render(<App />,document.getElementById("map"));








