const exampleCoverById = {
  1: '/api/files/example/nuomi.png',
  2: '/api/files/example/tuanzi.png',
  3: '/api/files/example/lanmei.png',
  4: '/api/files/example/juzi.png',
  5: '/api/files/example/naigai.png',
  6: '/api/files/example/yangguang.png',
  7: '/api/files/example/shandian.png',
  8: '/api/files/example/douchai.png',
  9: '/api/files/example/duanduan.png',
  10: '/api/files/example/mianhuatang.png',
  11: '/api/files/example/xiaoxue.png',
  12: '/api/files/example/tangdou.png',
  13: '/api/files/example/xueqiu.png'
}

export function syncExamplePetImage (pet) {
  if (!pet || !exampleCoverById[pet.id]) return pet
  const coverImage = exampleCoverById[pet.id]
  const images = Array.isArray(pet.images)
    ? (pet.images.length ? pet.images.map((url, index) => index === 0 ? coverImage : url) : [coverImage])
    : pet.images
  return { ...pet, coverImage, images }
}
