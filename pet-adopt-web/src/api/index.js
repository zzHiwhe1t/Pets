import request from './request'
import { syncExamplePetImage } from '../utils/petImages'

export const authApi = {
  login: data => request.post('/auth/login', data), register: data => request.post('/auth/register', data), logout: () => request.post('/auth/logout')
}
export const categoryApi = { tree: () => request.get('/categories/tree') }
export const petApi = {
  list: async params => {
    const data = await request.get('/pets', { params })
    return { ...data, records: data.records.map(syncExamplePetImage) }
  },
  detail: async id => syncExamplePetImage(await request.get(`/pets/${id}`)),
  create: data => request.post('/pets', data), update: (id, data) => request.put(`/pets/${id}`, data),
  status: (id, status) => request.put(`/pets/${id}/status`, { status }), remove: id => request.delete(`/pets/${id}`)
}
export const userApi = { me: () => request.get('/users/me'), update: data => request.put('/users/me', data) }
export const adoptionApi = {
  apply: data => request.post('/applications', data), sent: () => request.get('/applications/sent'),
  received: () => request.get('/applications/received'), review: (id, data) => request.put(`/applications/${id}/review`, data)
}
export const chatApi = {
  send: data => request.post('/chat/messages', data), thread: params => request.get('/chat/messages', { params }),
  conversations: () => request.get('/chat/conversations'), unread: () => request.get('/chat/unread-count')
}
