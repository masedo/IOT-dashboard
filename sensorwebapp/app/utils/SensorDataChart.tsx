
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface SensorData {
    temperature: number;
    humidity: number;
    pressure: number;
    Timestamp: string;
    co2: number;
}

interface SensorDataMap {
    [deviceId: string]: SensorData[];
}

const options = {
    scales: {
        x: {
            ticks: {
                color: 'white', // Makes x-axis labels white
            },
            grid: {
                color: 'rgba(255, 255, 255, 0.1)', // Optional: changes the grid line colors
            }
        },
        y: {
            ticks: {
                color: 'white', // Makes y-axis labels white
            },
            grid: {
                color: 'rgba(255, 255, 255, 0.1)', // Optional: changes the grid line colors
            }
        }
    },
    plugins: {
        legend: {
            labels: {
                color: 'white', // Makes legend text white
            }
        }
    }
};


export function dataChart(data: SensorDataMap) {
    

    const charts = Object.keys(data).map(deviceId => {
        const timestamps = data[deviceId].map(item => item.Timestamp);
        const temperatures = data[deviceId].map(item => item.temperature);
        const humidities = data[deviceId].map(item => item.humidity);
        const co2levels = data[deviceId].map(item => item.co2);
        const pressurelevels = data[deviceId].map(item => item.pressure);

        const temperatureData = {
            labels: timestamps,
            datasets: [{ label: 'Temperature (°C)', data: temperatures, borderColor: 'rgb(255, 99, 132)', backgroundColor: 'rgba(255, 99, 132, 0.5)', }],
        };
        const humidityData = {
            labels: timestamps,
            datasets: [{ label: 'Humidity (%)', data: humidities, borderColor: 'rgb(53, 162, 235)', backgroundColor: 'rgba(53, 162, 235, 0.5)', }],
        };
        const co2Data = {
            labels: timestamps,
            datasets: [{ label: 'CO2 (ppm)', data: co2levels, borderColor: 'rgb(75, 192, 192)', backgroundColor: 'rgba(75, 192, 192, 0.5)', }],
        };
        const pressureData = {
            labels: timestamps,
            datasets: [{ label: 'Pressure (hPa)', data: pressurelevels, borderColor: 'rgb(255, 205, 86)', backgroundColor: 'rgba(255, 205, 86, 0.5)', }],
        };
        

        return [
            <div className="bg-gray-600 rounded-lg p-4 mb-4" key={`${deviceId}-temperature`}>
                <Line data={temperatureData} options={options} />
            </div>,
            <div className="bg-gray-600 rounded-lg p-4 mb-4" key={`${deviceId}-humidity`}>
                <Line data={humidityData} options={options} />
            </div>,
            <div className="bg-gray-600 rounded-lg p-4 mb-4" key={`${deviceId}-co2`}>
                <Line data={co2Data} options={options} />
            </div>,
            <div className="bg-gray-600 rounded-lg p-4 mb-4" key={`${deviceId}-pressure`}>
                <Line data={pressureData} options={options} />
            </div>
        ];
    });

    return (
        <div className="bg-gray-700 rounded-lg p-4">
            {charts}
        </div>
    );
}
