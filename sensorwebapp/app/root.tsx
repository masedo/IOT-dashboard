import '~/styles/tailwind.css';
import type { MetaFunction } from "@remix-run/node";

import { cssBundleHref } from "@remix-run/css-bundle";
import type { LinksFunction } from "@remix-run/node";
import {
  Links,
  LiveReload,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from "@remix-run/react";

export const links: LinksFunction = () => [
  ...(cssBundleHref ? [{ rel: "stylesheet", href: cssBundleHref }] : []),
];

export const meta: MetaFunction = () => {
  return [
    { title: "IOT Sensor Dashboard" },
    { name: "description", content: "Welcome to IOT Sensor Dashboard" },
  ];
};

export default function App() {
  return (
    <html lang="en" className="bg-gray-900">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body className="font-sans bg-gray-800 text-white">
        <nav className="bg-gray-900 p-4">
          <div className="container mx-auto flex justify-between items-center">
            <a href="/" className="text-xl font-bold">IOT Sensor Dashboard</a>
            <div>
              <a href="https://github.com/Milesight-IoT/SensorDecoders/tree/main/EM_Series/EM500_Series/EM500-CO2" className="px-4 hover:text-blue-300">About</a>
              <a href="/logout" className="px-4 hover:text-blue-300">Logout</a>
            </div>
          </div>
        </nav>

        <main className="container mx-auto my-8 p-4 rounded-lg">
          <Outlet />
        </main>

        <ScrollRestoration />
        <Scripts />
        <LiveReload />
      </body>
    </html>
  );
}