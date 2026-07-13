-- 萌宠领养寄养平台 MySQL 8.0 初始化脚本
-- Navicat 中直接运行本文件即可；重复执行会重建 pet_adopt 数据库。
DROP DATABASE IF EXISTS pet_adopt;
CREATE DATABASE pet_adopt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE pet_adopt;

CREATE TABLE user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  username VARCHAR(30) NOT NULL COMMENT '登录用户名',
  password VARCHAR(64) NOT NULL COMMENT 'SHA-256密码摘要',
  nickname VARCHAR(30) NOT NULL COMMENT '用户昵称',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(80) DEFAULT NULL COMMENT '电子邮箱',
  avatar VARCHAR(255) DEFAULT '/api/files/default-avatar.png' COMMENT '头像地址',
  role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER普通用户/ADMIN管理员',
  token VARCHAR(64) DEFAULT NULL COMMENT '当前登录令牌',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), UNIQUE KEY uk_username (username), KEY idx_token (token)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE pet_category (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类主键',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0为大类',
  name VARCHAR(30) NOT NULL COMMENT '分类名称',
  code VARCHAR(40) NOT NULL COMMENT '分类编码',
  image VARCHAR(255) DEFAULT NULL COMMENT '分类展示图地址',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), UNIQUE KEY uk_code (code), KEY idx_parent (parent_id)
) ENGINE=InnoDB COMMENT='宠物双层分类表';

CREATE TABLE pet (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物主键',
  owner_id BIGINT NOT NULL COMMENT '发布人用户ID',
  category_id BIGINT NOT NULL COMMENT '宠物大类ID',
  subcategory_id BIGINT NOT NULL COMMENT '宠物细分类ID',
  name VARCHAR(40) NOT NULL COMMENT '宠物名称',
  breed VARCHAR(60) NOT NULL COMMENT '品种名称',
  age_months INT NOT NULL COMMENT '年龄（月）',
  gender VARCHAR(10) NOT NULL COMMENT '性别：MALE/FEMALE/UNKNOWN',
  weight DECIMAL(6,2) DEFAULT NULL COMMENT '体重（千克）',
  personality VARCHAR(255) DEFAULT NULL COMMENT '性格描述',
  vaccine_status VARCHAR(50) DEFAULT NULL COMMENT '疫苗情况',
  deworm_status VARCHAR(50) DEFAULT NULL COMMENT '驱虫情况',
  sterilization_status VARCHAR(50) DEFAULT NULL COMMENT '绝育情况',
  health_status VARCHAR(255) NOT NULL COMMENT '健康情况',
  feeding_notes TEXT NOT NULL COMMENT '饲养注意事项',
  owner_message TEXT DEFAULT NULL COMMENT '主人寄语',
  cover_image VARCHAR(255) NOT NULL COMMENT '封面图片地址',
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE待领养/IN_PROGRESS领养中/ADOPTED已领养/OFFLINE已下架',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), KEY idx_owner (owner_id), KEY idx_category (category_id,subcategory_id), KEY idx_status (status)
) ENGINE=InnoDB COMMENT='宠物发布表';

CREATE TABLE pet_image (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片主键',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  image_url VARCHAR(255) NOT NULL COMMENT '本地图片访问地址',
  sort INT NOT NULL DEFAULT 0 COMMENT '图片顺序，0为封面',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), KEY idx_pet (pet_id)
) ENGINE=InnoDB COMMENT='宠物多图表';

CREATE TABLE adoption_application (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请主键',
  pet_id BIGINT NOT NULL COMMENT '申请领养的宠物ID',
  applicant_id BIGINT NOT NULL COMMENT '申请人用户ID',
  owner_id BIGINT NOT NULL COMMENT '宠物发布人用户ID',
  reason VARCHAR(500) NOT NULL COMMENT '领养原因',
  living_condition VARCHAR(500) NOT NULL COMMENT '居住条件',
  experience VARCHAR(500) DEFAULT NULL COMMENT '养宠经验',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待审核/APPROVED已通过/REJECTED已驳回/CANCELLED已失效',
  review_remark VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  review_time DATETIME DEFAULT NULL COMMENT '审核时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), UNIQUE KEY uk_pet_applicant (pet_id,applicant_id), KEY idx_owner_status (owner_id,status)
) ENGINE=InnoDB COMMENT='领养申请表';

CREATE TABLE chat_message (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  pet_id BIGINT NOT NULL COMMENT '关联宠物ID',
  sender_id BIGINT NOT NULL COMMENT '发送人用户ID',
  receiver_id BIGINT NOT NULL COMMENT '接收人用户ID',
  content VARCHAR(1000) NOT NULL COMMENT '消息正文',
  read_flag TINYINT NOT NULL DEFAULT 0 COMMENT '已读标记：0未读1已读',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id), KEY idx_dialog (pet_id,sender_id,receiver_id), KEY idx_receiver_read (receiver_id,read_flag)
) ENGINE=InnoDB COMMENT='在线留言消息表';

-- 密码：普通测试账号均为 123456，管理员账号为 admin123。
INSERT INTO user(id,username,password,nickname,phone,email,avatar,role) VALUES
(1,'alice','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','暖心寄养人','13800000001','alice@example.com','/api/files/buoumao.jpg','USER'),
(2,'bob','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','认真领养人','13800000002','bob@example.com','/api/files/jinmao.jpg','USER'),
(3,'admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','平台管理员','13800000000','admin@example.com','/api/files/chaiquan.jpg','ADMIN');

-- 8 个大类。
INSERT INTO pet_category(id,parent_id,name,code,image,sort) VALUES
(1,0,'猫','CAT','/api/files/buoumao.jpg',1),(2,0,'狗','DOG','/api/files/jinmao.jpg',2),
(3,0,'仓鼠','HAMSTER','/api/files/cangshu.png',3),(4,0,'兔子','RABBIT','/api/files/tuzi.png',4),
(5,0,'鸟类','BIRD','/api/files/niao.png',5),(6,0,'爬宠','REPTILE','/api/files/reptile_gecko_01.png',6),
(7,0,'水族','AQUATIC','/api/files/aquatic_betta_01.png',7),(8,0,'异宠','EXOTIC','/api/files/exotic_hedgehog_01.png',8);

-- 猫细分类。
INSERT INTO pet_category(id,parent_id,name,code,image,sort) VALUES
(101,1,'布偶猫','CAT_RAGDOLL','/api/files/buoumao.jpg',1),(102,1,'英国短毛猫','CAT_BRITISH_SHORTHAIR','/api/files/yingduan.jpg',2),
(103,1,'美国短毛猫','CAT_AMERICAN_SHORTHAIR','/api/files/cat_american_01.png',3),(104,1,'暹罗猫','CAT_SIAMESE','/api/files/xieluomao.jpg',4),
(105,1,'狸花猫','CAT_TABBY','/api/files/cat_tabby_01.png',5),(106,1,'橘猫','CAT_ORANGE','/api/files/jumao.jpg',6),(107,1,'奶牛猫','CAT_TUXEDO','/api/files/nainiumao.jpg',7);
-- 狗细分类。
INSERT INTO pet_category(id,parent_id,name,code,image,sort) VALUES
(201,2,'金毛','DOG_GOLDEN','/api/files/jinmao.jpg',1),(202,2,'哈士奇','DOG_HUSKY','/api/files/hashiqi.jpg',2),
(203,2,'柴犬','DOG_SHIBA','/api/files/chaiquan.jpg',3),(204,2,'柯基','DOG_CORGI','/api/files/keji.jpg',4),
(205,2,'比熊','DOG_BICHON','/api/files/bixiong.jpg',5),(206,2,'萨摩耶','DOG_SAMOYED','/api/files/samoye.jpg',6),(207,2,'马尔济斯','DOG_MALTESE','/api/files/maerjisi.jpg',7);
-- 仓鼠、兔子、鸟类、爬宠、水族、异宠细分类。
INSERT INTO pet_category(id,parent_id,name,code,image,sort) VALUES
(301,3,'金丝熊','HAMSTER_GOLDEN','/api/files/hamster_golden_01.png',1),(302,3,'三线仓鼠','HAMSTER_DWARF','/api/files/hamster_dwarf_01.png',2),(303,3,'一线仓鼠','HAMSTER_CAMPBELL','/api/files/hamster_campbell_01.png',3),(304,3,'公婆仓鼠','HAMSTER_ROBOROVSKI','/api/files/hamster_robo_01.png',4),
(401,4,'垂耳兔','RABBIT_LOP','/api/files/rabbit_lop_01.png',1),(402,4,'侏儒兔','RABBIT_DWARF','/api/files/rabbit_dwarf_01.png',2),(403,4,'安哥拉兔','RABBIT_ANGORA','/api/files/rabbit_angora_01.png',3),(404,4,'狮子兔','RABBIT_LIONHEAD','/api/files/rabbit_lionhead_01.png',4),
(501,5,'玄凤鹦鹉','BIRD_COCKATIEL','/api/files/bird_cockatiel_01.png',1),(502,5,'虎皮鹦鹉','BIRD_BUDGIE','/api/files/bird_budgie_01.png',2),(503,5,'牡丹鹦鹉','BIRD_LOVEBIRD','/api/files/bird_lovebird_01.png',3),(504,5,'文鸟','BIRD_FINCH','/api/files/bird_finch_01.png',4),(505,5,'和尚鹦鹉','BIRD_MONK','/api/files/bird_monk_01.png',5),
(601,6,'鬃狮蜥','REPTILE_DRAGON','/api/files/reptile_dragon_01.png',1),(602,6,'豹纹守宫','REPTILE_GECKO','/api/files/reptile_gecko_01.png',2),(603,6,'陆龟','REPTILE_TORTOISE','/api/files/reptile_tortoise_01.png',3),(604,6,'玉米蛇','REPTILE_CORN_SNAKE','/api/files/reptile_cornsnake_01.png',4),
(701,7,'金鱼','AQUATIC_GOLDFISH','/api/files/aquatic_goldfish_01.png',1),(702,7,'锦鲤','AQUATIC_KOI','/api/files/aquatic_koi_01.png',2),(703,7,'孔雀鱼','AQUATIC_GUPPY','/api/files/aquatic_guppy_01.png',3),(704,7,'斗鱼','AQUATIC_BETTA','/api/files/aquatic_betta_01.png',4),(705,7,'观赏虾','AQUATIC_SHRIMP','/api/files/aquatic_shrimp_01.png',5),
(801,8,'刺猬','EXOTIC_HEDGEHOG','/api/files/exotic_hedgehog_01.png',1),(802,8,'蜜袋鼯','EXOTIC_SUGAR_GLIDER','/api/files/exotic_glider_01.png',2),(803,8,'龙猫','EXOTIC_CHINCHILLA','/api/files/exotic_chinchilla_01.png',3),(804,8,'雪貂','EXOTIC_FERRET','/api/files/exotic_ferret_01.png',4),(805,8,'豚鼠','EXOTIC_GUINEA_PIG','/api/files/exotic_guineapig_01.png',5);

INSERT INTO pet(id,owner_id,category_id,subcategory_id,name,breed,age_months,gender,weight,personality,vaccine_status,deworm_status,sterilization_status,health_status,feeding_notes,owner_message,cover_image,status,view_count) VALUES
(1,1,1,101,'糯米','布偶猫',18,'FEMALE',4.20,'温柔亲人，喜欢安静陪伴','疫苗齐全','已定期驱虫','已绝育','健康，近期体检正常','每日梳毛，注意肠胃敏感','希望你把她当作家人。','/api/files/example/nuomi.png','AVAILABLE',126),
(2,1,1,102,'团子','英国短毛猫',24,'MALE',5.60,'沉稳粘人','疫苗齐全','已驱虫','已绝育','健康','控制体重，少量多餐','它会在门口等你回家。','/api/files/example/tuanzi.png','AVAILABLE',98),
(3,1,1,104,'蓝莓','暹罗猫',10,'FEMALE',3.10,'活泼爱说话','已接种核心疫苗','已驱虫','未绝育','健康','需要较多互动和玩具','愿你们成为彼此最好的朋友。','/api/files/example/lanmei.png','IN_PROGRESS',76),
(4,1,1,106,'橘子','中华田园橘猫',30,'MALE',6.20,'亲人贪吃','疫苗齐全','已驱虫','已绝育','轻微超重','需要科学控粮并增加运动','是个让人安心的大暖男。','/api/files/example/juzi.png','AVAILABLE',141),
(5,1,1,107,'奶盖','奶牛猫',14,'FEMALE',3.80,'机灵好奇','疫苗齐全','已驱虫','已绝育','健康','封窗饲养，不散养','请给这个小机灵鬼一个稳定的家。','/api/files/example/naigai.png','AVAILABLE',63),
(6,1,2,201,'阳光','金毛',20,'MALE',27.50,'热情友善','疫苗齐全','已驱虫','已绝育','健康','每天至少外出运动两次','它的快乐一定会感染你。','/api/files/example/yangguang.png','AVAILABLE',203),
(7,1,2,202,'闪电','哈士奇',16,'MALE',22.00,'精力旺盛，亲人','疫苗齐全','已驱虫','未绝育','健康','需要大量运动和牢固牵引','有经验且有时间陪伴者优先。','/api/files/example/shandian.png','AVAILABLE',187),
(8,1,2,203,'豆柴','柴犬',28,'FEMALE',9.40,'独立稳重','疫苗齐全','已驱虫','已绝育','健康','外出必须牵引，耐心建立信任','慢热，但认定你就很忠诚。','/api/files/example/douchai.png','ADOPTED',112),
(9,1,2,204,'短短','柯基',13,'MALE',10.20,'开朗亲人','疫苗齐全','已驱虫','未绝育','健康','注意腰椎，减少频繁上下楼','期待一个爱散步的新家。','/api/files/example/duanduan.png','AVAILABLE',156),
(10,1,2,205,'棉花糖','比熊',9,'FEMALE',4.10,'粘人活泼','完成基础疫苗','已驱虫','未绝育','健康','定期美容和泪痕护理','喜欢被抱抱的小云朵。','/api/files/example/mianhuatang.png','AVAILABLE',88),
(11,1,2,206,'小雪','萨摩耶',26,'FEMALE',23.50,'温和爱笑','疫苗齐全','已驱虫','已绝育','健康','掉毛较多，需要每日梳理','愿它的微笑照亮你的生活。','/api/files/example/xiaoxue.png','AVAILABLE',171),
(12,1,2,207,'糖豆','马尔济斯',18,'MALE',3.50,'安静依赖人','疫苗齐全','已驱虫','已绝育','健康','注意口腔和被毛护理','适合愿意温柔陪伴的家庭。','/api/files/example/tangdou.png','AVAILABLE',69),
(13,1,1,101,'雪球','布偶猫',36,'MALE',5.80,'安静温顺','疫苗齐全','已驱虫','已绝育','健康','不建议频繁更换环境','希望下一站就是永远。','/api/files/example/xueqiu.png','OFFLINE',45);

INSERT INTO pet_image(pet_id,image_url,sort)
SELECT id,cover_image,0 FROM pet;

INSERT INTO adoption_application(id,pet_id,applicant_id,owner_id,reason,living_condition,experience,status) VALUES
(1,3,2,1,'喜欢暹罗猫，能长期稳定照顾。','自有住房，已封窗，家人同意。','有三年养猫经验。','PENDING');

INSERT INTO chat_message(pet_id,sender_id,receiver_id,content,read_flag,create_time) VALUES
(3,2,1,'你好，我很喜欢蓝莓，想了解一下它平时的作息。',1,DATE_SUB(NOW(),INTERVAL 30 MINUTE)),
(3,1,2,'你好，蓝莓白天比较安静，晚上会活跃一些。',0,DATE_SUB(NOW(),INTERVAL 20 MINUTE));
