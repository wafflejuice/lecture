import './globals.css'
import {Inter} from 'next/font/google'
import Link from 'next/link'
import LoginButton from "@/app/LoginButton";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

const inter = Inter({subsets: ['latin']})

export const metadata = {
  title: 'Create Next App',
  description: 'Generated by create next app',
}

export default async function RootLayout({children}) {
  let session = await getServerSession(authOptions);
  console.log(session);
  
  return (
    <html lang="en">
    <body>
    <div className="navbar">
      <Link href="/" className="logo">Appleforum</Link>
      <Link href="/list">List</Link>
      <LoginButton/>
    </div>
    {children}
    </body>
    </html>
  )
}
