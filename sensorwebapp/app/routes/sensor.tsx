// app/routes/sensor.tsx
import { LoaderFunction, redirect } from '@remix-run/node';
import { useLoaderData } from '@remix-run/react';
import { json } from '@remix-run/server-runtime';
import { dataChart } from '~/utils/SensorDataChart';
import { sessionStorage, validateSession } from '~/utils/auth'; 
import { API_BASE_URL } from '../config/config'; 

export const loader: LoaderFunction = async ({ request }) => {

    const isValidSession = await validateSession(request);
    if (!isValidSession) {
        throw redirect('/login');
    }
    
    try {
        const sensorData = await fetchSensorData(request);
        const latestSensorData = await fetchLatestSensorData(request);
        return json({ sensorData, latestSensorData });
    } catch (error) {
        console.error('Error fetching sensor data:', error);
        throw new Response('Error fetching data', { status: 500 });
    }
};

async function fetchLatestSensorData(request: Request) {
    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    const jwtToken = session.get('jwtToken');

    const response = await fetch(`${API_BASE_URL}/lastsensordata`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Cookie': request.headers.get('Cookie') || '',
        },
    });

    if (!response.ok) {
        throw new Error('Failed to fetch latest sensor data');
    }

    return response.json();
}


async function fetchSensorData(request: Request) {
    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    const jwtToken = session.get('jwtToken');

    const response = await fetch(`${API_BASE_URL}/sensordata`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`, 
            'Cookie': request.headers.get('Cookie') || '',
        },
    });

    if (!response.ok) {
        throw new Error('Failed to fetch sensor data');
    }

    return response.json();
}

export default function SensorDataComponent() {
    const { sensorData, latestSensorData } = useLoaderData() as any;

    const charts = dataChart(sensorData);

    return (
        <div className="bg-gray-700 rounded-lg p-4">
            <h2 className="px-3 text-2xl font-bold mb-4 text-purple-500">Latest Sensor Data</h2>
            <div className="px-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
                {Object.keys(latestSensorData).map(deviceId => (
                    <div key={deviceId} className="bg-gray-600 rounded-lg p-4 text-white">
                        <h3 className="mb-4">Device: {deviceId}</h3>
                        <p>Temperature: {latestSensorData[deviceId].temperature}°C</p>
                        <p>Humidity: {latestSensorData[deviceId].humidity}%</p>
                        <p>Pressure: {latestSensorData[deviceId].pressure}hPa</p>
                        <p>CO2: {latestSensorData[deviceId].co2}ppm</p>
                        <p>Timestamp: {latestSensorData[deviceId].Timestamp}</p>
                    </div>
                ))}
            </div>
            <h2 className="px-3 text-2xl font-bold mb-4 text-purple-500">Sensor Data History</h2>
            {charts}
        </div>
    );
}

