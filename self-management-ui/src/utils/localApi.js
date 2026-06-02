export const getLocalKey = (name) => `self-management-${name}`

const readJson = (key, fallback) => {
  try {
    const value = localStorage.getItem(key)
    return value ? JSON.parse(value) : fallback
  } catch {
    return fallback
  }
}

const writeJson = (key, value) => {
  localStorage.setItem(key, JSON.stringify(value))
}

const today = () => new Date().toISOString().slice(0, 10)
const daysAgo = (offset) => {
  const date = new Date()
  date.setDate(date.getDate() - offset)
  return date.toISOString().slice(0, 10)
}
const monthStr = (date = new Date()) => `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`

export const seedIfNeeded = () => {
  if (!localStorage.getItem(getLocalKey('seeded'))) {
    const users = [
      { id: 1, username: 'admin', password: 'admin123', nickname: 'system-admin', avatar: '', major: 'Computer Science', createTime: new Date().toISOString(), updateTime: new Date().toISOString(), role: 'ADMIN', status: 1 },
      { id: 4, username: 'test01', password: '123456', nickname: 'test01', avatar: '', major: 'Mathematics', createTime: new Date().toISOString(), updateTime: new Date().toISOString(), role: 'USER', status: 1 }
    ]
    writeJson(getLocalKey('users'), users)
    writeJson(getLocalKey('plans-1'), [
      { id: 101, userId: 1, title: '完成项目首页', content: '把主页和导航整理好', deadline: today(), status: '进行中' },
      { id: 102, userId: 1, title: '学习英语', content: '背单词 100 个', deadline: today(), status: '未开始' }
    ])
    writeJson(getLocalKey('courses-1'), [
      { id: 201, userId: 1, period: '第一节', monday: { courseName: '高等数学', location: 'A101', teacher: '张老师' }, tuesday: null, wednesday: { courseName: '英语', location: 'B203', teacher: '李老师' }, thursday: null, friday: { courseName: 'Java', location: 'C302', teacher: '王老师' }, saturday: null, sunday: null }
    ])
    writeJson(getLocalKey('exams-1'), [
      { id: 301, userId: 1, examName: '期中考试', examDate: today(), remark: '认真复习' }
    ])
    writeJson(getLocalKey('finances-1'), [
      { id: 401, userId: 1, recordDate: today(), type: '收入', category: '兼职收入', amount: 500, remark: '本月兼职' },
      { id: 402, userId: 1, recordDate: today(), type: '支出', category: '餐饮美食', amount: 68, remark: '午饭' }
    ])
    writeJson(getLocalKey('focus-1'), [
      { date: today(), total: 90 },
      { date: daysAgo(1), total: 60 },
      { date: daysAgo(2), total: 120 },
      { date: daysAgo(3), total: 40 },
      { date: daysAgo(4), total: 75 },
      { date: daysAgo(5), total: 30 },
      { date: daysAgo(6), total: 100 }
    ])
    writeJson(getLocalKey('weight-1'), [
      { id: 501, userId: 1, recordDate: today(), weight: 65.2 },
      { id: 502, userId: 1, recordDate: daysAgo(1), weight: 65.5 },
      { id: 503, userId: 1, recordDate: daysAgo(2), weight: 65.1 }
    ])
    localStorage.setItem(getLocalKey('seeded'), '1')
    localStorage.setItem('user', JSON.stringify(users[0]))
  }
}

const response = (data, status = 200) => Promise.resolve({
  data,
  status,
  statusText: status === 200 ? 'OK' : 'ERROR',
  headers: {},
  config: {},
  request: {}
})
const success = (data = null, msg = '操作成功') => ({ code: 200, msg, data })
const error = (msg = '操作失败') => ({ code: 500, msg, data: null })
const nextId = (items) => items.reduce((max, item) => Math.max(max, Number(item.id) || 0), 0) + 1
const listByUser = (key, userId) => readJson(getLocalKey(key), []).filter(item => Number(item.userId) === Number(userId))

const statsFromPlans = (userId) => {
  const plans = listByUser(`plans-${userId}`, userId)
  const counts = { '未开始': 0, '进行中': 0, '已完成': 0 }
  plans.forEach(plan => { counts[plan.status] = (counts[plan.status] || 0) + 1 })
  return Object.entries(counts).map(([name, value]) => ({ name, value }))
}

export const installLocalAxiosAdapter = (axios) => {
  axios.defaults.adapter = async (config) => {
    seedIfNeeded()
    const method = (config.method || 'get').toLowerCase()
    const url = config.url || ''
    const params = config.params || {}
    const body = typeof config.data === 'string' ? JSON.parse(config.data) : (config.data || {})
    const userId = Number(params.userId || body.userId || body.id || 1)

    if (url === '/user/login' && method === 'post') {
      const users = readJson(getLocalKey('users'), [])
      const user = users.find(item => item.username === body.username)
      if (!user) return response(error('账号不存在'))
      if (user.password !== body.password) return response(error('密码错误'))
      if (user.status === 0) return response(error('账号已被封禁'))
      return response(success(user, '登录成功'))
    }

    if (url === '/user/register' && method === 'post') {
      const users = readJson(getLocalKey('users'), [])
      if (users.some(item => item.username === body.username)) return response(error('账号已存在'))
      const user = { id: nextId(users), username: body.username, password: body.password, nickname: body.nickname, avatar: '', major: '', createTime: new Date().toISOString(), updateTime: new Date().toISOString(), role: 'USER', status: 1 }
      users.push(user)
      writeJson(getLocalKey('users'), users)
      return response(success(user, '注册成功'))
    }

    if (url === '/user/list' && method === 'get') return response(success(readJson(getLocalKey('users'), [])))
    if (url.startsWith('/user/promote/') && method === 'put') {
      const id = Number(url.split('/').pop())
      const users = readJson(getLocalKey('users'), [])
      const user = users.find(item => item.id === id)
      if (!user) return response(error('用户不存在'))
      user.role = 'ADMIN'
      writeJson(getLocalKey('users'), users)
      return response(success(user, '提升成功'))
    }
    if (url.startsWith('/user/status/') && method === 'put') {
      const [, , , id, status] = url.split('/')
      const users = readJson(getLocalKey('users'), [])
      const user = users.find(item => item.id === Number(id))
      if (!user) return response(error('用户不存在'))
      user.status = Number(status)
      writeJson(getLocalKey('users'), users)
      return response(success(user, '状态更新成功'))
    }
    if (url === '/user/update' && method === 'put') {
      const users = readJson(getLocalKey('users'), [])
      const user = users.find(item => item.id === Number(body.id))
      if (!user) return response(error('用户不存在'))
      Object.assign(user, body, { updateTime: new Date().toISOString() })
      writeJson(getLocalKey('users'), users)
      return response(success(user, '更新成功'))
    }
    if (url === '/user/password' && method === 'put') {
      const users = readJson(getLocalKey('users'), [])
      const user = users.find(item => item.id === Number(body.id))
      if (!user) return response(error('用户不存在'))
      if (user.password !== body.oldPassword) return response(error('原密码错误'))
      user.password = body.newPassword
      writeJson(getLocalKey('users'), users)
      return response(success(true, '密码修改成功'))
    }

    const handleCrud = (storeName, transform = item => item, normalize = item => item) => {
      const key = getLocalKey(`${storeName}-${userId}`)
      const items = readJson(key, [])
      return { key, items, transform, normalize }
    }

    const planStore = handleCrud('plans')
    if (url === '/plan/list' && method === 'get') return response(success(planStore.items.filter(item => Number(item.userId) === userId)))
    if (url === '/plan/add' && method === 'post') {
      const plan = { ...body, id: nextId(planStore.items), userId }
      planStore.items.unshift(plan)
      writeJson(planStore.key, planStore.items)
      return response(success(plan, '新增成功'))
    }
    if (url === '/plan/update' && method === 'put') {
      const idx = planStore.items.findIndex(item => Number(item.id) === Number(body.id))
      if (idx >= 0) planStore.items[idx] = { ...planStore.items[idx], ...body }
      else planStore.items.unshift({ ...body, id: nextId(planStore.items), userId })
      writeJson(planStore.key, planStore.items)
      return response(success(true, '更新成功'))
    }
    if (url.startsWith('/plan/') && method === 'delete') {
      const id = Number(url.split('/').pop())
      writeJson(planStore.key, planStore.items.filter(item => Number(item.id) !== id))
      return response(success(true, '删除成功'))
    }
    if (url === '/plan/stats' && method === 'get') return response(success(statsFromPlans(userId)))

    const financeKey = getLocalKey(`finances-${userId}`)
    const financeItems = readJson(financeKey, [])
    if (url === '/finance/list' && method === 'get') return response(success(financeItems))
    if (url === '/finance/add' && method === 'post') {
      const item = { ...body, id: nextId(financeItems), userId }
      financeItems.unshift(item)
      writeJson(financeKey, financeItems)
      return response(success(item, '新增成功'))
    }
    if (url === '/finance/update' && method === 'put') {
      const idx = financeItems.findIndex(item => Number(item.id) === Number(body.id))
      if (idx >= 0) financeItems[idx] = { ...financeItems[idx], ...body }
      writeJson(financeKey, financeItems)
      return response(success(true, '更新成功'))
    }
    if (url.startsWith('/finance/') && method === 'delete') {
      const id = Number(url.split('/').pop())
      writeJson(financeKey, financeItems.filter(item => Number(item.id) !== id))
      return response(success(true, '删除成功'))
    }
    if (url === '/finance/stats' && method === 'get') {
      const months = [...new Set(financeItems.map(item => String(item.recordDate || '').slice(0, 7)))].filter(Boolean)
      const data = months.flatMap(month => {
        const income = financeItems.filter(item => item.type === '收入' && String(item.recordDate || '').slice(0, 7) === month).reduce((sum, item) => sum + Number(item.amount || 0), 0)
        const expense = financeItems.filter(item => item.type === '支出' && String(item.recordDate || '').slice(0, 7) === month).reduce((sum, item) => sum + Number(item.amount || 0), 0)
        return [{ month, type: '收入', total: income }, { month, type: '支出', total: expense }]
      })
      return response(success(data))
    }

    const focusKey = getLocalKey(`focus-${userId}`)
    const focusItems = readJson(focusKey, [])
    if (url === '/focus/stats' && method === 'get') {
      return response(success(focusItems.map(item => ({ date: item.date, total: item.total }))))
    }
    if ((url === '/focus/record' || url === '/focus/add') && method === 'post') {
      focusItems.unshift({ date: body.date || today(), total: Number(body.total || 0) })
      writeJson(focusKey, focusItems)
      return response(success(true, '记录成功'))
    }

    const courseKey = getLocalKey(`courses-${userId}`)
    const courseItems = readJson(courseKey, [])
    if (url === '/course/list' && method === 'get') return response(success(courseItems))
    if (url === '/course/add' && method === 'post') {
      const item = { ...body, id: nextId(courseItems), userId }
      courseItems.push(item)
      writeJson(courseKey, courseItems)
      return response(success(item, '新增成功'))
    }
    if (url === '/course/update' && method === 'put') {
      const idx = courseItems.findIndex(item => Number(item.id) === Number(body.id))
      if (idx >= 0) courseItems[idx] = { ...courseItems[idx], ...body }
      writeJson(courseKey, courseItems)
      return response(success(true, '更新成功'))
    }
    if (url === '/course/clear/' + userId && method === 'delete') {
      writeJson(courseKey, [])
      return response(success(true, '清空成功'))
    }
    if (url === '/course/import' && method === 'post') return response(success(true, '导入成功'))

    const examKey = getLocalKey(`exams-${userId}`)
    const examItems = readJson(examKey, [])
    if (url === '/exam/list' && method === 'get') return response(success(examItems))
    if (url === '/exam/add' && method === 'post') {
      const item = { ...body, id: nextId(examItems), userId }
      examItems.unshift(item)
      writeJson(examKey, examItems)
      return response(success(item, '新增成功'))
    }
    if (url === '/exam/update' && method === 'put') {
      const idx = examItems.findIndex(item => Number(item.id) === Number(body.id))
      if (idx >= 0) examItems[idx] = { ...examItems[idx], ...body }
      writeJson(examKey, examItems)
      return response(success(true, '更新成功'))
    }
    if (url.startsWith('/exam/') && method === 'delete') {
      const id = Number(url.split('/').pop())
      writeJson(examKey, examItems.filter(item => Number(item.id) !== id))
      return response(success(true, '删除成功'))
    }
    if (url === '/exam/stats' && method === 'get') return response(success([]))

    const weightKey = getLocalKey(`weight-${userId}`)
    const weightItems = readJson(weightKey, [])
    if (url === '/weight/list' && method === 'get') return response(success(weightItems))
    if ((url === '/weight/add' || url === '/weight/upload') && method === 'post') {
      const item = { ...body, id: nextId(weightItems), userId }
      weightItems.unshift(item)
      writeJson(weightKey, weightItems)
      return response(success(item, '新增成功'))
    }
    if (url.startsWith('/weight/') && method === 'delete') {
      const id = Number(url.split('/').pop())
      writeJson(weightKey, weightItems.filter(item => Number(item.id) !== id))
      return response(success(true, '删除成功'))
    }

    return response(error(`未实现的本地接口: ${method.toUpperCase()} ${url}`))
  }
}
