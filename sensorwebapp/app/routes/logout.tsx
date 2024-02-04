// app/routes/logout.tsx

import { LoaderFunction, redirect } from '@remix-run/node';
import { logout } from '~/utils/auth';

export const loader: LoaderFunction = async ({ request }) => {
  await logout(request);

  return redirect('/login');
};
