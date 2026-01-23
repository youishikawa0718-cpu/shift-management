import { useState, useEffect, useCallback, useMemo } from 'react'

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [page, setPage] = useState(() => localStorage.getItem('token') ? 'home' : 'login')

  const user = useMemo(() => {
    if (!token) return null
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return { email: payload.sub, role: payload.role }
    } catch {
      return null
    }
  }, [token])

  const handleLogout = () => {
    localStorage.removeItem('token')
    setToken(null)
    setPage('login')
  }

  if (!token) {
    return <LoginPage setToken={setToken} />
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-blue-600 text-white p-4">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-xl font-bold">シフト管理システム</h1>
          <div className="flex items-center gap-4">
            <span>{user?.email} ({user?.role === 'MANAGER' ? '店長' : 'アルバイト'})</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 px-4 py-2 rounded hover:bg-red-600"
            >
              ログアウト
            </button>
          </div>
        </div>
      </nav>

      <div className="container mx-auto p-4">
        <div className="flex gap-4 mb-4">
          <button
            onClick={() => setPage('home')}
            className={`px-4 py-2 rounded ${page === 'home' ? 'bg-blue-600 text-white' : 'bg-white'}`}
          >
            ホーム
          </button>
          <button
            onClick={() => setPage('shift-request')}
            className={`px-4 py-2 rounded ${page === 'shift-request' ? 'bg-blue-600 text-white' : 'bg-white'}`}
          >
            シフト申請
          </button>
          <button
            onClick={() => setPage('my-shifts')}
            className={`px-4 py-2 rounded ${page === 'my-shifts' ? 'bg-blue-600 text-white' : 'bg-white'}`}
          >
            マイシフト
          </button>
          {user?.role === 'MANAGER' && (
            <>
              <button
                onClick={() => setPage('approve')}
                className={`px-4 py-2 rounded ${page === 'approve' ? 'bg-blue-600 text-white' : 'bg-white'}`}
              >
                申請承認
              </button>
              <button
                onClick={() => setPage('employees')}
                className={`px-4 py-2 rounded ${page === 'employees' ? 'bg-blue-600 text-white' : 'bg-white'}`}
              >
                従業員管理
              </button>
            </>
          )}
        </div>

        {page === 'home' && <HomePage user={user} />}
        {page === 'shift-request' && <ShiftRequestPage token={token} />}
        {page === 'my-shifts' && <MyShiftsPage token={token} />}
        {page === 'approve' && <ApprovePage token={token} />}
        {page === 'employees' && <EmployeesPage token={token} />}
      </div>
    </div>
  )
}

function LoginPage({ setToken }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      if (!res.ok) {
        throw new Error('ログインに失敗しました')
      }
      const data = await res.json()
      localStorage.setItem('token', data.accessToken)
      setToken(data.accessToken)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded shadow-md w-96">
        <h1 className="text-2xl font-bold mb-6 text-center">シフト管理システム</h1>
        <form onSubmit={handleLogin}>
          <div className="mb-4">
            <label className="block text-gray-700 mb-2">メールアドレス</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div className="mb-4">
            <label className="block text-gray-700 mb-2">パスワード</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          {error && <p className="text-red-500 mb-4">{error}</p>}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
          >
            ログイン
          </button>
        </form>
      </div>
    </div>
  )
}

function HomePage({ user }) {
  return (
    <div className="bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">ようこそ！</h2>
      <p>ログイン中: {user?.email}</p>
      <p>権限: {user?.role === 'MANAGER' ? '店長' : 'アルバイト'}</p>
    </div>
  )
}

function ShiftRequestPage({ token }) {
  const [date, setDate] = useState('')
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [message, setMessage] = useState('')
  const [requests, setRequests] = useState([])

  const fetchRequests = useCallback(async () => {
    const res = await fetch('/api/shift-requests/me', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      const data = await res.json()
      setRequests(data)
    }
  }, [token])

  useEffect(() => {
    fetchRequests()
  }, [fetchRequests])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage('')
    try {
      const res = await fetch('/api/shift-requests', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ date, startTime, endTime })
      })
      if (!res.ok) {
        const err = await res.json()
        throw new Error(err.message || '申請に失敗しました')
      }
      setMessage('申請しました')
      setDate('')
      setStartTime('')
      setEndTime('')
      fetchRequests()
    } catch (err) {
      setMessage(err.message)
    }
  }

  return (
    <div className="bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">シフト申請</h2>
      <form onSubmit={handleSubmit} className="mb-6">
        <div className="grid grid-cols-3 gap-4 mb-4">
          <div>
            <label className="block text-gray-700 mb-2">日付</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 mb-2">開始時刻</label>
            <input
              type="time"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 mb-2">終了時刻</label>
            <input
              type="time"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
              className="w-full p-2 border rounded"
              required
            />
          </div>
        </div>
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          申請する
        </button>
        {message && <p className="mt-2 text-green-600">{message}</p>}
      </form>

      <h3 className="text-lg font-bold mb-2">申請履歴</h3>
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">日付</th>
            <th className="border p-2">時間</th>
            <th className="border p-2">ステータス</th>
          </tr>
        </thead>
        <tbody>
          {requests.map((req) => (
            <tr key={req.id}>
              <td className="border p-2">{req.date}</td>
              <td className="border p-2">{req.startTime} - {req.endTime}</td>
              <td className="border p-2">
                <span className={`px-2 py-1 rounded text-sm ${
                  req.status === 'APPROVED' ? 'bg-green-200' :
                  req.status === 'REJECTED' ? 'bg-red-200' : 'bg-yellow-200'
                }`}>
                  {req.status === 'APPROVED' ? '承認' : req.status === 'REJECTED' ? '却下' : '申請中'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function MyShiftsPage({ token }) {
  const [shifts, setShifts] = useState([])

  useEffect(() => {
    const fetchShifts = async () => {
      const res = await fetch('/api/shifts/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (res.ok) {
        const data = await res.json()
        setShifts(data)
      }
    }
    fetchShifts()
  }, [token])

  return (
    <div className="bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">マイシフト</h2>
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">日付</th>
            <th className="border p-2">予定</th>
            <th className="border p-2">実績</th>
          </tr>
        </thead>
        <tbody>
          {shifts.map((shift) => (
            <tr key={shift.id}>
              <td className="border p-2">{shift.date}</td>
              <td className="border p-2">{shift.startTime} - {shift.endTime}</td>
              <td className="border p-2">
                {shift.actualStartTime && shift.actualEndTime
                  ? `${shift.actualStartTime} - ${shift.actualEndTime}`
                  : '-'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function ApprovePage({ token }) {
  const [requests, setRequests] = useState([])

  const fetchRequests = useCallback(async () => {
    const res = await fetch('/api/shift-requests/pending', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.ok) {
      const data = await res.json()
      setRequests(data)
    }
  }, [token])

  useEffect(() => {
    fetchRequests()
  }, [fetchRequests])

  const handleApprove = async (id) => {
    await fetch(`/api/shift-requests/${id}/approve`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    fetchRequests()
  }

  const handleReject = async (id) => {
    await fetch(`/api/shift-requests/${id}/reject`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    fetchRequests()
  }

  return (
    <div className="bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">シフト申請承認</h2>
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">従業員</th>
            <th className="border p-2">日付</th>
            <th className="border p-2">時間</th>
            <th className="border p-2">操作</th>
          </tr>
        </thead>
        <tbody>
          {requests.map((req) => (
            <tr key={req.id}>
              <td className="border p-2">{req.userName}</td>
              <td className="border p-2">{req.date}</td>
              <td className="border p-2">{req.startTime} - {req.endTime}</td>
              <td className="border p-2">
                <button
                  onClick={() => handleApprove(req.id)}
                  className="bg-green-500 text-white px-3 py-1 rounded mr-2 hover:bg-green-600"
                >
                  承認
                </button>
                <button
                  onClick={() => handleReject(req.id)}
                  className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                >
                  却下
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function EmployeesPage({ token }) {
  const [employees, setEmployees] = useState([])

  useEffect(() => {
    const fetchEmployees = async () => {
      const res = await fetch('/api/employees', {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (res.ok) {
        const data = await res.json()
        setEmployees(data)
      }
    }
    fetchEmployees()
  }, [token])

  return (
    <div className="bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">従業員管理</h2>
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">名前</th>
            <th className="border p-2">メール</th>
            <th className="border p-2">ステータス</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id}>
              <td className="border p-2">{emp.name}</td>
              <td className="border p-2">{emp.email}</td>
              <td className="border p-2">
                <span className={`px-2 py-1 rounded text-sm ${emp.isActive ? 'bg-green-200' : 'bg-red-200'}`}>
                  {emp.isActive ? '有効' : '無効'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default App