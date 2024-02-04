// app/routes/login.tsx

import { json, ActionFunction, redirect } from '@remix-run/node';
import { Form, useActionData } from '@remix-run/react';
import { login } from '~/utils/auth'; 
import { sessionStorage } from '~/utils/auth'; 


interface ActionData {
  error?: string;
}

export const action: ActionFunction = async ({ request }) => {
  const formData = await request.formData();
  const username = formData.get('username') as string;
  const password = formData.get('password') as string;

  try {
    const jwtToken = await login(username, password);

    if (!jwtToken) {
      return json({ error: 'Invalid credentials' }, { status: 400 });
    }
    
    const cookieHeader = request.headers.get("Cookie");
    const session = await sessionStorage.getSession(cookieHeader);

    session.set('jwtToken', jwtToken); 

    return redirect('/sensor', {
      headers: {
        'Set-Cookie': await sessionStorage.commitSession(session, {
          secure: false,
        }),
      },
    });
  } catch (e) {
    return json({ error: 'An error occurred' });
  }
};


export default function LoginPage() {
  const actionData = useActionData<ActionData>();

  return (
    <div className="bg-gray-800 rounded-lg p-6 min-h-screen flex flex-col items-center justify-start">
      <Form method="post">
        <div className="mb-4">
          <label className="text-xl font-bold text-white" htmlFor="username">
            Username:
          </label>
          <input
            className="font-sans bg-gray-700 text-white rounded-lg p-2 w-64"
            type="text"
            id="username"
            name="username"
            required
          />
        </div>
        <div className="mb-4">
          <label className="text-xl font-bold text-white" htmlFor="password">
            Password:
          </label>
          <input
            className="font-sans bg-gray-700 text-white rounded-lg p-2 w-64"
            type="password"
            id="password"
            name="password"
            required
          />
        </div>
        {actionData?.error && <p className="text-red-500">{actionData.error}</p>}
        <button className="bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg p-2 w-64" type="submit">
          Login
        </button>
      </Form>
    </div>
  );
}
