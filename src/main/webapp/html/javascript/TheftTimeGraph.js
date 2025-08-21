import React, { Component } from 'react'
import axios from 'axios';
import { Line } from 'react-chartjs-2';
export class TheftTimeGraph extends Component {
    constructor(props) {
        super(props);
        this.state = {
            theft: '',
            time: '',
        };
    }
    componentDidMount() {
        axios.get(``)
            .then(res => {
                console.log(res);
                const ipl = res.data;
                let theft_amount = [];
                let time = [];
                ipl.forEach(record => {
                    theft_amount.push(record.Theft);
                    time.push(record.Week);
                });
                this.setState({
                    Data: {
                        labels: theft_amount,
                        datasets: [
                            {
                                label: 'Stolen Item per Week',
                                data: time
                        }
                    ]
                }
            });
        })
    }

    render() {
        return (
            <div>
                <h2> TRIAL PLS WORK</h2>
                <button>
                    {this.props.render()}
                </button>
                <Line
                    data={this.state.Data}
                    options={{ maintainAspectRatio: false }} />
            </div>
        )
    }
}
export default TheftTimeGraph;
