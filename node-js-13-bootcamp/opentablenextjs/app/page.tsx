import { Inter } from 'next/font/google'
import styles from './page.module.css'

const inter = Inter({ subsets: ["latin"] })

export default function Home() {
  return (
    <main className={styles.main}>
      <h1 className="mt-2 text-blue-400 text-7xl">Hello guys</h1>
    </main>
  )
}
