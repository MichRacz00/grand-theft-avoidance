let baseUri = "http://localhost:8080/GTA_VI/rest/";
let articles;
let chart;


function getData() {
    //No need to send GET requests if sessionStorage implemented
    if(!sessionStorage.getItem("articlesCache")) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            var tableData;
            if (this.readyState === 4 && this.status === 200) {
                articles = JSON.parse(this.responseText);
                tableData = [];
                //Useful linq method to get no. of thefts grouped by article type from raw
                tableData = Enumerable.From(articles).GroupBy("$.article", null,
                    function (key, g) {
                        return {
                            article: key,
                            num: g.Sum("$.num"),
                        }
                    }).ToArray();
                sessionStorage.setItem("articlesCache", JSON.stringify(tableData));
                makeTable(tableData);
            }
        }
        xhr.open("GET", baseUri + "articles", true);
        xhr.send();
    }
    else {
        makeTable(JSON.parse(sessionStorage.getItem("articlesCache")));
    }
}

let lockToken;
getToken();
getData();

function makeTable(tableData){
    var link = function(cell, formatterParams, onRendered){
        return "<a href = \"javascript:getDetails(" + cell.getRow().getData().article + ")\"><i class='fa fa-bar-chart'></i></a>";
    };

    var table = new Tabulator("#table", {
        data: tableData,
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 25, 50, 100, true],
        columns: [
            {title: "Article ID", field: "article", headerFilter: "input", width: 330, hozAlign: "center"},
            {title: "Shoplifts", field: "num", headerFilter: "number", headerFilterFunc: ">=", width: 330, hozAlign: "center"},
            {title: "Stats",formatter:link, headerSort:false, hozAlign: "center", width: 300},
        ]
    });

    document.getElementById("download-csv").addEventListener("click", function(){
        table.download("csv", "ArticleData.csv");
    });

    document.getElementById("download-json").addEventListener("click", function(){
        table.download("json", "ArticleData.json");
    });

    document.getElementById("download-xlsx").addEventListener("click", function(){
        table.download("xlsx", "ArticleData.xlsx", {sheetName:"My Data"});
    });
}

function getDetails(id) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if(this.readyState === 4 && this.status === 200) {
            let myObj = JSON.parse(this.responseText);
            //Getting count for group by date
            let count = myObj.reduce(function(r,row) {
                r[row.date] = ++r[row.date] || 1;
                return r;
            }, {});
            let dataset = Object.keys(count).map(function (key) {
                return {x: key, y: count[key]};
            });

            var table = document.getElementById("table_buttons");
            table.style.transition = "marginLeft 0.7s";
            table.style.marginLeft = "0%";
            //using this jQuery DOM methods instead of .destroy()
            $("#chart").remove();
            $("#graph").append('<canvas id ="chart" width="600px" height = "400px"></canvas>')

            let ctx = document.getElementById('chart').getContext('2d');
            let timeFormat = "DD-MM-YY";
            chart = new Chart(ctx,{
                type: 'line',
                data: {
                    datasets: [{
                        label: 'Article ' + id,
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
                            text: 'Theft statistics for Article ' + id
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
    }
    xhr.open("GET",baseUri + "thefts?article=" + id );
    xhr.send();
}

function getToken() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            if (JSON.stringify(myObj) === "false") {
                window.location.href = "login.html";
            }
            storeId = myObj.store;
            permission = myObj.permission;
            lockToken = false;
            createSidebar();
        }
    }
    let uri = baseUri + "login/getId?token=" + localStorage.getItem("token");
    xhr.open("GET", uri);
    xhr.send();
}


function createSidebar(){

    var analytics = document.getElementById("analytics");
    var title = document.getElementById("title");
    if(permission === 1){
        analytics.setAttribute("onclick", "location.href='storemanager.html'")
        title.innerHTML = "Store Manager";
    }else if(permission === 2){
        title.innerText = "Division Manager"
        var ul = document.getElementById("navigation")
        var li = document.createElement('li');
        li.className = "unselected-tab";
        var icon = document.createElement('i');
        icon.className = 'bx bx-map';
        var name = document.createElement('span');
        name.appendChild(document.createTextNode("Locations"));
        name.className = "tab-description";
        li.appendChild(icon);
        li.appendChild(name);
        li.setAttribute("onclick", "location.href='divisionmanager.html'")
        ul.insertBefore(li, ul.children[2]);
        analytics.setAttribute("onclick", "location.href='locations.html'")
    }
}
