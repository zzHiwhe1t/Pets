<template>
  <article class="pet-card" @click="$router.push(`/pets/${pet.id}`)">
    <div class="pet-cover"><img :src="pet.coverImage" :alt="pet.name" @error="imageFallback"><el-tag class="status-tag" :type="status.type" effect="dark" size="small">{{ status.text }}</el-tag></div>
    <div class="pet-card-body">
      <div class="pet-title-row"><h3>{{ pet.name }}</h3><span class="gender" :class="pet.gender === 'MALE' ? 'male' : 'female'">{{ genderText(pet.gender) }}</span></div>
      <p class="breed">{{ pet.subcategoryName || pet.breed }} · {{ ageText(pet.ageMonths) }}</p>
      <p class="personality"><i class="el-icon-magic-stick" /> {{ pet.personality || '等待你来认识它' }}</p>
      <div class="card-foot"><span><i class="el-icon-user" /> {{ pet.ownerNickname }}</span><span><i class="el-icon-view" /> {{ pet.viewCount || 0 }}</span></div>
    </div>
  </article>
</template>
<script>
import { petStatus, genderText, ageText, imageFallback } from '../utils/format'
export default { props: { pet: { type: Object, required: true } }, computed: { status () { return petStatus[this.pet.status] || {} } }, methods: { genderText, ageText, imageFallback } }
</script>
