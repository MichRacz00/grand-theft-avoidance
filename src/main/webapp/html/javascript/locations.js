let baseUri = "http://localhost:8080/GTA_VI/rest/";
getToken();

let chart;
let tableData;

function getToken() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            if (JSON.stringify(myObj) === "false" || myObj.permission !== 2) {
                window.location.href = "login.html";
            }
        }
    }
    let uri = baseUri + "login/getId?token=" + localStorage.getItem("token");
    xhr.open("GET", uri);
    xhr.send();
}

function getData() {
    if(!sessionStorage.getItem("tableCache")) {
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            var myObj;
            if (this.readyState === 4 && this.status === 200) {
                myObj = JSON.parse(this.responseText);
                tableData = [];
                for (const x in myObj) {
                    let temp = {"id": myObj[x].id, "num": myObj[x].thefts};
                    tableData.push(temp);
                }
                console.log("No cache send request");
                sessionStorage.setItem("tableCache", JSON.stringify(tableData));
            }
            makeTable(tableData);
        }
        xhr.open("GET", baseUri + "stores", true);
        xhr.send();
    }
    else {
        tableData = JSON.parse(sessionStorage.getItem("tableCache"));
        makeTable(tableData);
    }
}
getData();

function makeTable(tableData) {

    const link = function (cell, formatterParams, onRendered) {
        return "<a href = \"javascript:showOnMap(" + cell.getRow().getData().id + ")\"><i class='fa fa-map-marker'></i></a>";
    };

    const link1 = function (cell, formatterParams, onRendered) {
        return "<a href = \"javascript:getDetails(" + cell.getRow().getData().id + ")\"><i class='fa fa-bar-chart'></i></a>";
    };

    //TODO icon to be changed to more distinct one
    const link2 = function (cell, formatterParams, onRendered) {
        return "<a href = \"javascript:showAnalytics(" + cell.getRow().getData().id + ")\"><i class='bx bxs-store'></i></a>"
    }

    let table = new Tabulator("#table",{
        data: tableData,
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 25, 50, 100, true],
        columns: [
            {title: "Store ID", field: "id", headerFilter: "input", width: 200, hozAlign: "center"},
            {
                title: "Shoplifts",
                field: "num",
                headerFilter: "number",
                headerFilterFunc: ">=",
                width: 200,
                hozAlign: "center"
            },
            {title: "Location", formatter:link, headerSort:false, hozAlign: "center", width: 200},
            {title: "Stats",formatter:link1, headerSort:false, hozAlign: "center", width: 160},
            {title: "Insights",formatter:link2, headerSort:false, hozAlign: "center", width: 180}
        ]
    });
    document.getElementById("download-csv").addEventListener("click", function(){
        table.download("csv", "StoreData.csv");
    });
    document.getElementById("download-json").addEventListener("click", function(){
        table.download("json", "StoreData.json");
    });
    document.getElementById("download-xlsx").addEventListener("click", function(){
        table.download("xlsx", "StoreData.xlsx", {sheetName:"My Data"});
    });
}

function showOnMap(id) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if(this.readyState === 4 && this.status === 200) {
            let myObj = JSON.parse(this.responseText);
            let lat = myObj.latitude;
            let lng = myObj.longitude;
            $("#mapModal").empty();
            $("#mapModal").append("<h2>Store" + id + "</h2><iframe width=\"450\" height=\"450\" style=\"border:0;\" loading=\"lazy\""
                + "src=\"https://www.google.com/maps/embed/v1/place?q=" + lat + "%2C" + lng + "&key=AIzaSyDIgIqBNO3M78Q6TrYPLU0uqDJRz3Iim54&zoom=8\"></iframe>")
            $("#mapModal").modal();
        }
    }
    xhr.open("GET",baseUri + "stores/" + id );
    xhr.send();
}

function getDetails(id) {
    //No need to send GET requests if sessionStorage implemented
    if(!sessionStorage.getItem("chartCache" + id)) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (this.readyState === 4 && this.status === 200) {
                let myObj = JSON.parse(this.responseText);
                //Getting count for group by date
                let count = myObj.reduce(function (r, row) {
                    r[row.date] = ++r[row.date] || 1;
                    return r;
                }, {});
                //Make suitable JSON object
                let dataset = Object.keys(count).map(function (key) {
                    return {x: key, y: count[key]};
                });
                makeChart(dataset, id);
                sessionStorage.setItem("chartData" + id, JSON.stringify(dataset));
            }
        }
        xhr.open("GET", baseUri + "thefts?store=" + id);
        xhr.send();
    }
    else {
        let dataset = sessionStorage.getItem("chartData" + id);
        makeChart(JSON.parse(dataset), id);
    }
}

function showAnalytics(id) {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 204) {
            window.location.href = "storemanager.html";
        }
    }
    let uri = baseUri + "login/modifyStoreId?token=" + localStorage.getItem("token") + "&tempstore=" + id;
    xhr.open("PUT", uri);
    xhr.send();
}

function makeChart(dataset,id) {
    var table = document.getElementById("table_buttons");
    table.style.transition = "marginLeft 0.7s";
    table.style.marginLeft = "0%";
    $("#chart").remove();
    $("#graph").append('<canvas id ="chart" width="640px" height = "480px"></canvas>')

    let ctx = document.getElementById('chart').getContext('2d');
    let timeFormat = "DD-MM-YY";
    chart = new Chart(ctx,{
        type: 'line',
        data: {
            datasets: [{
                label: 'Store ' + id,
                data: dataset,
                borderColor: [
                    'rgba(255,108,55, 1)'
                ],
                backgroundColor: ['rgba(255,108,55, 1)'],
                borderWidth: 1
            }],
            options: {
                responsive: true,
                title: {
                    display: true,
                    text: 'Theft statistics for Store ' + id
                },
                scales: {
                    xAxes: [{
                        type:       "time",
                        time:       {
                            format: timeFormat,
                            tooltipFormat: 'll',
                        },
                        gridLines: {
                            display: false
                        },
                        scaleLabel: {
                            display:     true,
                            labelString: 'Date'
                        }
                    }],
                    yAxes: [{
                        scaleLabel: {
                            display:     true,
                            labelString: 'value'
                        },
                        type: 'linear',
                        ticks: {
                            min: 0,
                            beginAtZero: true,
                            stepSize: 1,
                        }}]
                }
            }
        },
    })
}
