export const petStatus = {
  AVAILABLE: { text: '待领养', type: 'success' }, IN_PROGRESS: { text: '领养中', type: 'warning' },
  ADOPTED: { text: '已领养', type: 'info' }, OFFLINE: { text: '已下架', type: 'danger' }
}
export const appStatus = {
  PENDING: { text: '待审核', type: 'warning' }, APPROVED: { text: '已通过', type: 'success' },
  REJECTED: { text: '已驳回', type: 'danger' }, CANCELLED: { text: '已失效', type: 'info' }
}
export const genderText = value => ({ MALE: '弟弟', FEMALE: '妹妹', UNKNOWN: '未知' }[value] || value)
export const ageText = months => months < 12 ? `${months}个月` : `${Math.floor(months / 12)}岁${months % 12 ? months % 12 + '个月' : ''}`
export const imageFallback = event => { if (!event.target.dataset.fallback) { event.target.dataset.fallback = '1'; event.target.src = '/api/files/buoumao.jpg' } }
