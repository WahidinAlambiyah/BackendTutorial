/*
 Navicat Premium Data Transfer

 Source Server         : PostgreSQL Local
 Source Server Type    : PostgreSQL
 Source Server Version : 160003 (160003)
 Source Host           : localhost:5432
 Source Catalog        : monolith
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160003 (160003)
 File Encoding         : 65001

 Date: 12/10/2024 01:30:20
*/


-- ----------------------------
-- Sequence structure for jhi_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."jhi_user_id_seq";
CREATE SEQUENCE "public"."jhi_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1050
CACHE 1;

-- ----------------------------
-- Sequence structure for job_history_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."job_history_id_seq";
CREATE SEQUENCE "public"."job_history_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for location_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."location_id_seq";
CREATE SEQUENCE "public"."location_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_city_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_city_id_seq";
CREATE SEQUENCE "public"."mst_city_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_country_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_country_id_seq";
CREATE SEQUENCE "public"."mst_country_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_department_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_department_id_seq";
CREATE SEQUENCE "public"."mst_department_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_district_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_district_id_seq";
CREATE SEQUENCE "public"."mst_district_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_employee_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_employee_id_seq";
CREATE SEQUENCE "public"."mst_employee_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_job_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_job_id_seq";
CREATE SEQUENCE "public"."mst_job_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_postal_code_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_postal_code_id_seq";
CREATE SEQUENCE "public"."mst_postal_code_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_province_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_province_id_seq";
CREATE SEQUENCE "public"."mst_province_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_region_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_region_id_seq";
CREATE SEQUENCE "public"."mst_region_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_service_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_service_id_seq";
CREATE SEQUENCE "public"."mst_service_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_sub_district_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_sub_district_id_seq";
CREATE SEQUENCE "public"."mst_sub_district_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for mst_task_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mst_task_id_seq";
CREATE SEQUENCE "public"."mst_task_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for trx_event_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."trx_event_id_seq";
CREATE SEQUENCE "public"."trx_event_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for trx_testimonial_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."trx_testimonial_id_seq";
CREATE SEQUENCE "public"."trx_testimonial_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Sequence structure for trx_tournament_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."trx_tournament_id_seq";
CREATE SEQUENCE "public"."trx_tournament_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1;

-- ----------------------------
-- Table structure for databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS "public"."databasechangelog";
CREATE TABLE "public"."databasechangelog" (
  "id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "author" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "filename" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "dateexecuted" timestamp(6) NOT NULL,
  "orderexecuted" int4 NOT NULL,
  "exectype" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "md5sum" varchar(35) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "comments" varchar(255) COLLATE "pg_catalog"."default",
  "tag" varchar(255) COLLATE "pg_catalog"."default",
  "liquibase" varchar(20) COLLATE "pg_catalog"."default",
  "contexts" varchar(255) COLLATE "pg_catalog"."default",
  "labels" varchar(255) COLLATE "pg_catalog"."default",
  "deployment_id" varchar(10) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of databasechangelog
-- ----------------------------
INSERT INTO "public"."databasechangelog" VALUES ('00000000000001', 'jhipster', 'config/liquibase/changelog/00000000000000_initial_schema.xml', '2024-09-24 13:39:28.664195', 1, 'EXECUTED', '9:792ed4e40e4a698b9cc9506d5abf4972', 'createTable tableName=jhi_user; createTable tableName=jhi_authority; createTable tableName=jhi_user_authority; addPrimaryKey tableName=jhi_user_authority; addForeignKeyConstraint baseTableName=jhi_user_authority, constraintName=fk_authority_name, ...', '', NULL, '4.27.0', NULL, NULL, '7159968407');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093317-1', 'jhipster', 'config/liquibase/changelog/20240924093317_added_entity_TrxEvent.xml', '2024-09-24 16:42:27.907552', 2, 'EXECUTED', '9:7750e5936acf3b09fd135730db47e386', 'createTable tableName=trx_event; dropDefaultValue columnName=date, tableName=trx_event', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093317-1-data', 'jhipster', 'config/liquibase/changelog/20240924093317_added_entity_TrxEvent.xml', '2024-09-24 16:42:28.094902', 3, 'EXECUTED', '9:3a2c99341e262afa6930eb7f2de96bc3', 'loadData tableName=trx_event', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093318-1', 'jhipster', 'config/liquibase/changelog/20240924093318_added_entity_TrxTournament.xml', '2024-09-24 16:42:28.142643', 4, 'EXECUTED', '9:f8505dd5c554d820006f1642b5f8af03', 'createTable tableName=trx_tournament; dropDefaultValue columnName=start_date, tableName=trx_tournament; dropDefaultValue columnName=end_date, tableName=trx_tournament', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093318-1-data', 'jhipster', 'config/liquibase/changelog/20240924093318_added_entity_TrxTournament.xml', '2024-09-24 16:42:28.217098', 5, 'EXECUTED', '9:fa9b004c27812c2493d76abf8506d47d', 'loadData tableName=trx_tournament', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093319-1', 'jhipster', 'config/liquibase/changelog/20240924093319_added_entity_MstService.xml', '2024-09-24 16:42:28.353088', 6, 'EXECUTED', '9:14ad260c68a8077263969b39a4b32e1f', 'createTable tableName=mst_service', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093319-1-data', 'jhipster', 'config/liquibase/changelog/20240924093319_added_entity_MstService.xml', '2024-09-24 16:42:28.469882', 7, 'EXECUTED', '9:2ddfe273fde7f712e4cfb5298239bc99', 'loadData tableName=mst_service', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093320-1', 'jhipster', 'config/liquibase/changelog/20240924093320_added_entity_TrxTestimonial.xml', '2024-09-24 16:42:28.519092', 8, 'EXECUTED', '9:3222edcbccedf257e0fa515ab4854470', 'createTable tableName=trx_testimonial; dropDefaultValue columnName=date, tableName=trx_testimonial', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093320-1-data', 'jhipster', 'config/liquibase/changelog/20240924093320_added_entity_TrxTestimonial.xml', '2024-09-24 16:42:28.639812', 9, 'EXECUTED', '9:a990319d297837e72c3dc23aa1b4cdca', 'loadData tableName=trx_testimonial', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093321-1', 'jhipster', 'config/liquibase/changelog/20240924093321_added_entity_MstRegion.xml', '2024-09-24 16:42:28.744404', 10, 'EXECUTED', '9:7f183ddb5bdd74fbcadf4bd7ec5f0c62', 'createTable tableName=mst_region', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093321-1-data', 'jhipster', 'config/liquibase/changelog/20240924093321_added_entity_MstRegion.xml', '2024-09-24 16:42:28.862328', 11, 'EXECUTED', '9:d5ec27fb83a63acef969946ca75eaa8e', 'loadData tableName=mst_region', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093322-1', 'jhipster', 'config/liquibase/changelog/20240924093322_added_entity_MstCountry.xml', '2024-09-24 16:42:28.922048', 12, 'EXECUTED', '9:4c606893e4794512242ebd4e196143ce', 'createTable tableName=mst_country', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093322-1-data', 'jhipster', 'config/liquibase/changelog/20240924093322_added_entity_MstCountry.xml', '2024-09-24 16:42:28.974499', 13, 'EXECUTED', '9:58352a78e7373fdfec92a4852f77d127', 'loadData tableName=mst_country', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093323-1', 'jhipster', 'config/liquibase/changelog/20240924093323_added_entity_MstProvince.xml', '2024-09-24 16:42:29.018717', 14, 'EXECUTED', '9:49bff23f7d2023ddc980f2a20a8ff296', 'createTable tableName=mst_province', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093323-1-data', 'jhipster', 'config/liquibase/changelog/20240924093323_added_entity_MstProvince.xml', '2024-09-24 16:42:29.065437', 15, 'EXECUTED', '9:5bf8880c85fece92781a4eb0da830c5f', 'loadData tableName=mst_province', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093324-1', 'jhipster', 'config/liquibase/changelog/20240924093324_added_entity_MstCity.xml', '2024-09-24 16:42:29.111277', 16, 'EXECUTED', '9:79e3b857842f71e00642e010d92b0d08', 'createTable tableName=mst_city', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093324-1-data', 'jhipster', 'config/liquibase/changelog/20240924093324_added_entity_MstCity.xml', '2024-09-24 16:42:29.15194', 17, 'EXECUTED', '9:f647257f0a3b8438387d2858a01805ab', 'loadData tableName=mst_city', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093325-1', 'jhipster', 'config/liquibase/changelog/20240924093325_added_entity_MstDistrict.xml', '2024-09-24 16:42:29.194763', 18, 'EXECUTED', '9:4b8d633b3b4d5019b9a3e6ebb1891eea', 'createTable tableName=mst_district', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093325-1-data', 'jhipster', 'config/liquibase/changelog/20240924093325_added_entity_MstDistrict.xml', '2024-09-24 16:42:29.240967', 19, 'EXECUTED', '9:e80604de9c4825afff673b5a33be3075', 'loadData tableName=mst_district', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093326-1', 'jhipster', 'config/liquibase/changelog/20240924093326_added_entity_MstSubDistrict.xml', '2024-09-24 16:42:29.280092', 20, 'EXECUTED', '9:46380753ddcea521bcdc48bcd36a003c', 'createTable tableName=mst_sub_district', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093326-1-data', 'jhipster', 'config/liquibase/changelog/20240924093326_added_entity_MstSubDistrict.xml', '2024-09-24 16:42:29.320413', 21, 'EXECUTED', '9:24ce198e478372ea1ed1397074da441d', 'loadData tableName=mst_sub_district', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093327-1', 'jhipster', 'config/liquibase/changelog/20240924093327_added_entity_MstPostalCode.xml', '2024-09-24 16:42:29.348461', 22, 'EXECUTED', '9:8fb971bea11ba7d68a69225aa1fc2d30', 'createTable tableName=mst_postal_code', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093327-1-data', 'jhipster', 'config/liquibase/changelog/20240924093327_added_entity_MstPostalCode.xml', '2024-09-24 16:42:29.390636', 23, 'EXECUTED', '9:de57f38abff07b8dabee5d15a7224e0a', 'loadData tableName=mst_postal_code', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093328-1', 'jhipster', 'config/liquibase/changelog/20240924093328_added_entity_Location.xml', '2024-09-24 16:42:29.419075', 24, 'EXECUTED', '9:922184f6fb0ef30ea2d1d5269b5c32ad', 'createTable tableName=location', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093328-1-data', 'jhipster', 'config/liquibase/changelog/20240924093328_added_entity_Location.xml', '2024-09-24 16:42:29.456878', 25, 'EXECUTED', '9:bee3483e15aa37317713e68ed2201b22', 'loadData tableName=location', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093329-1', 'jhipster', 'config/liquibase/changelog/20240924093329_added_entity_MstDepartment.xml', '2024-09-24 16:42:29.499129', 26, 'EXECUTED', '9:b76e46683fcf973958453a755c0f61d8', 'createTable tableName=mst_department', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093329-1-data', 'jhipster', 'config/liquibase/changelog/20240924093329_added_entity_MstDepartment.xml', '2024-09-24 16:42:29.546371', 27, 'EXECUTED', '9:cc4bb47fa69c784c880999d71744455a', 'loadData tableName=mst_department', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093330-1', 'jhipster', 'config/liquibase/changelog/20240924093330_added_entity_MstTask.xml', '2024-09-24 16:42:29.580964', 28, 'EXECUTED', '9:17e56a37715aa4be4bcf5b8923d32054', 'createTable tableName=mst_task', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093330-1-data', 'jhipster', 'config/liquibase/changelog/20240924093330_added_entity_MstTask.xml', '2024-09-24 16:42:29.67091', 29, 'EXECUTED', '9:78bdcad31ad3f45b0ae643cb2c54dfd5', 'loadData tableName=mst_task', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093331-1', 'jhipster', 'config/liquibase/changelog/20240924093331_added_entity_MstEmployee.xml', '2024-09-24 16:42:29.714384', 30, 'EXECUTED', '9:1048f9c55e895f7cc38e1716e399e045', 'createTable tableName=mst_employee; dropDefaultValue columnName=hire_date, tableName=mst_employee', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093331-1-data', 'jhipster', 'config/liquibase/changelog/20240924093331_added_entity_MstEmployee.xml', '2024-09-24 16:42:29.761034', 31, 'EXECUTED', '9:88998e9fb3db7658d19f79010db3f69d', 'loadData tableName=mst_employee', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093332-1', 'jhipster', 'config/liquibase/changelog/20240924093332_added_entity_MstJob.xml', '2024-09-24 16:42:29.801722', 32, 'EXECUTED', '9:968f06f908d1d9301611d423c018823b', 'createTable tableName=mst_job', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093332-1-relations', 'jhipster', 'config/liquibase/changelog/20240924093332_added_entity_MstJob.xml', '2024-09-24 16:42:29.831734', 33, 'EXECUTED', '9:fc4efef92fbfa434d2782387dcaa8e33', 'createTable tableName=rel_mst_job__task; addPrimaryKey tableName=rel_mst_job__task', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093332-1-data', 'jhipster', 'config/liquibase/changelog/20240924093332_added_entity_MstJob.xml', '2024-09-24 16:42:29.884982', 34, 'EXECUTED', '9:0c5ef44bef0559bb9b529a3495045ca0', 'loadData tableName=mst_job', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093333-1', 'jhipster', 'config/liquibase/changelog/20240924093333_added_entity_JobHistory.xml', '2024-09-24 16:42:29.937678', 35, 'EXECUTED', '9:e62e5894302a03975ea7f88da79cf140', 'createTable tableName=job_history; dropDefaultValue columnName=start_date, tableName=job_history; dropDefaultValue columnName=end_date, tableName=job_history', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093333-1-data', 'jhipster', 'config/liquibase/changelog/20240924093333_added_entity_JobHistory.xml', '2024-09-24 16:42:29.999257', 36, 'EXECUTED', '9:e9142a80daa1c788bed00da36c062d35', 'loadData tableName=job_history', '', NULL, '4.27.0', 'faker', NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093317-2', 'jhipster', 'config/liquibase/changelog/20240924093317_added_entity_constraints_TrxEvent.xml', '2024-09-24 16:42:30.025678', 37, 'EXECUTED', '9:6c6be79419632d3a76c7ab1f3e3a4e8f', 'addForeignKeyConstraint baseTableName=trx_event, constraintName=fk_trx_event__service_id, referencedTableName=mst_service; addForeignKeyConstraint baseTableName=trx_event, constraintName=fk_trx_event__testimonial_id, referencedTableName=trx_testim...', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093318-2', 'jhipster', 'config/liquibase/changelog/20240924093318_added_entity_constraints_TrxTournament.xml', '2024-09-24 16:42:30.045546', 38, 'EXECUTED', '9:d315efa963c01e789260eb008f81975e', 'addForeignKeyConstraint baseTableName=trx_tournament, constraintName=fk_trx_tournament__event_id, referencedTableName=trx_event', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093319-2', 'jhipster', 'config/liquibase/changelog/20240924093319_added_entity_constraints_MstService.xml', '2024-09-24 16:42:30.063581', 39, 'EXECUTED', '9:0c8f5979cc04aae716afe71605611578', 'addForeignKeyConstraint baseTableName=mst_service, constraintName=fk_mst_service__testimonial_id, referencedTableName=trx_testimonial', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093322-2', 'jhipster', 'config/liquibase/changelog/20240924093322_added_entity_constraints_MstCountry.xml', '2024-09-24 16:42:30.081971', 40, 'EXECUTED', '9:431267e4919aa18547925114174ec9af', 'addForeignKeyConstraint baseTableName=mst_country, constraintName=fk_mst_country__region_id, referencedTableName=mst_region', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093323-2', 'jhipster', 'config/liquibase/changelog/20240924093323_added_entity_constraints_MstProvince.xml', '2024-09-24 16:42:30.102721', 41, 'EXECUTED', '9:9a24e076840da3592b87a43589f4725e', 'addForeignKeyConstraint baseTableName=mst_province, constraintName=fk_mst_province__country_id, referencedTableName=mst_country', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093324-2', 'jhipster', 'config/liquibase/changelog/20240924093324_added_entity_constraints_MstCity.xml', '2024-09-24 16:42:30.120793', 42, 'EXECUTED', '9:ec6603302f5e46fe8abea1b7f565e145', 'addForeignKeyConstraint baseTableName=mst_city, constraintName=fk_mst_city__province_id, referencedTableName=mst_province', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093325-2', 'jhipster', 'config/liquibase/changelog/20240924093325_added_entity_constraints_MstDistrict.xml', '2024-09-24 16:42:30.137286', 43, 'EXECUTED', '9:3e6e35bc065b90f1908742740ed2ef6a', 'addForeignKeyConstraint baseTableName=mst_district, constraintName=fk_mst_district__city_id, referencedTableName=mst_city', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093326-2', 'jhipster', 'config/liquibase/changelog/20240924093326_added_entity_constraints_MstSubDistrict.xml', '2024-09-24 16:42:30.153374', 44, 'EXECUTED', '9:e129962a840debaddcb5f8f8be3911a7', 'addForeignKeyConstraint baseTableName=mst_sub_district, constraintName=fk_mst_sub_district__district_id, referencedTableName=mst_district', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093327-2', 'jhipster', 'config/liquibase/changelog/20240924093327_added_entity_constraints_MstPostalCode.xml', '2024-09-24 16:42:30.167253', 45, 'EXECUTED', '9:807ca87d519729e0b04637b966888b34', 'addForeignKeyConstraint baseTableName=mst_postal_code, constraintName=fk_mst_postal_code__sub_district_id, referencedTableName=mst_sub_district', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093329-2', 'jhipster', 'config/liquibase/changelog/20240924093329_added_entity_constraints_MstDepartment.xml', '2024-09-24 16:42:30.182092', 46, 'EXECUTED', '9:211e8bad4a8007b4d3c18e1597081e66', 'addForeignKeyConstraint baseTableName=mst_department, constraintName=fk_mst_department__location_id, referencedTableName=location', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093331-2', 'jhipster', 'config/liquibase/changelog/20240924093331_added_entity_constraints_MstEmployee.xml', '2024-09-24 16:42:30.199444', 47, 'EXECUTED', '9:11f06b10ea3ba6f3a709210af15ea99c', 'addForeignKeyConstraint baseTableName=mst_employee, constraintName=fk_mst_employee__manager_id, referencedTableName=mst_employee; addForeignKeyConstraint baseTableName=mst_employee, constraintName=fk_mst_employee__department_id, referencedTableNam...', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093332-2', 'jhipster', 'config/liquibase/changelog/20240924093332_added_entity_constraints_MstJob.xml', '2024-09-24 16:42:30.219336', 48, 'EXECUTED', '9:5a7c359ddd382d69f8d27e52f5c4707e', 'addForeignKeyConstraint baseTableName=rel_mst_job__task, constraintName=fk_rel_mst_job__task__mst_job_id, referencedTableName=mst_job; addForeignKeyConstraint baseTableName=rel_mst_job__task, constraintName=fk_rel_mst_job__task__task_id, reference...', '', NULL, '4.27.0', NULL, NULL, '7170947705');
INSERT INTO "public"."databasechangelog" VALUES ('20240924093333-2', 'jhipster', 'config/liquibase/changelog/20240924093333_added_entity_constraints_JobHistory.xml', '2024-09-24 16:42:30.241545', 49, 'EXECUTED', '9:330b6be2a70c3b9febe5af71b732089d', 'addForeignKeyConstraint baseTableName=job_history, constraintName=fk_job_history__job_id, referencedTableName=mst_job; addForeignKeyConstraint baseTableName=job_history, constraintName=fk_job_history__department_id, referencedTableName=mst_departm...', '', NULL, '4.27.0', NULL, NULL, '7170947705');

-- ----------------------------
-- Table structure for databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS "public"."databasechangeloglock";
CREATE TABLE "public"."databasechangeloglock" (
  "id" int4 NOT NULL,
  "locked" bool NOT NULL,
  "lockgranted" timestamp(6),
  "lockedby" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of databasechangeloglock
-- ----------------------------
INSERT INTO "public"."databasechangeloglock" VALUES (1, 'f', NULL, NULL);

-- ----------------------------
-- Table structure for jhi_authority
-- ----------------------------
DROP TABLE IF EXISTS "public"."jhi_authority";
CREATE TABLE "public"."jhi_authority" (
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;

-- ----------------------------
-- Records of jhi_authority
-- ----------------------------
INSERT INTO "public"."jhi_authority" VALUES ('ROLE_ADMIN');
INSERT INTO "public"."jhi_authority" VALUES ('ROLE_USER');

-- ----------------------------
-- Table structure for jhi_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."jhi_user";
CREATE TABLE "public"."jhi_user" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1050
CACHE 1
),
  "login" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "password_hash" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "first_name" varchar(50) COLLATE "pg_catalog"."default",
  "last_name" varchar(50) COLLATE "pg_catalog"."default",
  "email" varchar(191) COLLATE "pg_catalog"."default",
  "image_url" varchar(256) COLLATE "pg_catalog"."default",
  "activated" bool NOT NULL,
  "lang_key" varchar(10) COLLATE "pg_catalog"."default",
  "activation_key" varchar(20) COLLATE "pg_catalog"."default",
  "reset_key" varchar(20) COLLATE "pg_catalog"."default",
  "created_by" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "created_date" timestamp(6),
  "reset_date" timestamp(6),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6)
)
;

-- ----------------------------
-- Records of jhi_user
-- ----------------------------
INSERT INTO "public"."jhi_user" VALUES (1, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'Administrator', 'Administrator', 'admin@localhost', '', 't', 'en', NULL, NULL, 'system', NULL, NULL, 'system', NULL);
INSERT INTO "public"."jhi_user" VALUES (2, 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K', 'User', 'User', 'user@localhost', '', 't', 'en', NULL, NULL, 'system', NULL, NULL, 'system', NULL);

-- ----------------------------
-- Table structure for jhi_user_authority
-- ----------------------------
DROP TABLE IF EXISTS "public"."jhi_user_authority";
CREATE TABLE "public"."jhi_user_authority" (
  "user_id" int8 NOT NULL,
  "authority_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;

-- ----------------------------
-- Records of jhi_user_authority
-- ----------------------------
INSERT INTO "public"."jhi_user_authority" VALUES (1, 'ROLE_ADMIN');
INSERT INTO "public"."jhi_user_authority" VALUES (1, 'ROLE_USER');
INSERT INTO "public"."jhi_user_authority" VALUES (2, 'ROLE_USER');

-- ----------------------------
-- Table structure for job_history
-- ----------------------------
DROP TABLE IF EXISTS "public"."job_history";
CREATE TABLE "public"."job_history" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "start_date" timestamp(6),
  "end_date" timestamp(6),
  "language" varchar(255) COLLATE "pg_catalog"."default",
  "job_id" int8,
  "department_id" int8,
  "employee_id" int8
)
;

-- ----------------------------
-- Records of job_history
-- ----------------------------
INSERT INTO "public"."job_history" VALUES (1, '2024-09-23 17:25:07', '2024-09-23 20:56:54', 'FRENCH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (2, '2024-09-23 23:19:18', '2024-09-24 05:26:07', 'ENGLISH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (3, '2024-09-23 14:03:37', '2024-09-24 03:31:58', 'FRENCH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (4, '2024-09-23 13:52:49', '2024-09-24 02:56:13', 'FRENCH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (5, '2024-09-23 17:07:57', '2024-09-24 06:23:32', 'SPANISH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (6, '2024-09-24 08:25:03', '2024-09-23 11:16:01', 'SPANISH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (7, '2024-09-23 11:48:41', '2024-09-23 19:42:20', 'SPANISH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (8, '2024-09-23 17:12:39', '2024-09-23 16:14:31', 'FRENCH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (9, '2024-09-23 23:59:28', '2024-09-23 12:59:57', 'SPANISH', NULL, NULL, NULL);
INSERT INTO "public"."job_history" VALUES (10, '2024-09-24 05:49:14', '2024-09-23 13:07:38', 'ENGLISH', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for location
-- ----------------------------
DROP TABLE IF EXISTS "public"."location";
CREATE TABLE "public"."location" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "street_address" varchar(255) COLLATE "pg_catalog"."default",
  "postal_code" varchar(255) COLLATE "pg_catalog"."default",
  "city" varchar(255) COLLATE "pg_catalog"."default",
  "state_province" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of location
-- ----------------------------
INSERT INTO "public"."location" VALUES (1, 'cue', 'aw', 'Jefferyboro', 'less unless gladly');
INSERT INTO "public"."location" VALUES (2, 'old-fashioned fall suddenly', 'than mandolin superb', 'Haagworth', 'beyond stress');
INSERT INTO "public"."location" VALUES (3, 'overhang finally lapse', 'thwart', 'Emmerichton', 'over freely');
INSERT INTO "public"."location" VALUES (4, 'hopelessly outside transcribe', 'till um out', 'Fort Waltonview', 'grief unfinished');
INSERT INTO "public"."location" VALUES (5, 'rigidly wrongly against', 'smoothly modulo suborn', 'Cristianborough', 'link steer equally');
INSERT INTO "public"."location" VALUES (6, 'anenst', 'alongside', 'Midwest City', 'but');
INSERT INTO "public"."location" VALUES (7, 'yuck', 'who', 'Hoover', 'tan joyously');
INSERT INTO "public"."location" VALUES (8, 'trusty tub', 'hence verbally stow', 'Cartwrightshire', 'what among rotating');
INSERT INTO "public"."location" VALUES (9, 'buoy', 'where inwardly boo', 'El Paso', 'offensively before');
INSERT INTO "public"."location" VALUES (10, 'er drat yuck', 'scarily suit near', 'Lake Mohamedstead', 'harsh crossly');

-- ----------------------------
-- Table structure for mst_city
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_city";
CREATE TABLE "public"."mst_city" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default",
  "province_id" int8
)
;

-- ----------------------------
-- Records of mst_city
-- ----------------------------
INSERT INTO "public"."mst_city" VALUES (1, 'though fuse', 'hunter healthily till', 'velvety boo', NULL);
INSERT INTO "public"."mst_city" VALUES (2, 'rely', 'er', 'when', NULL);
INSERT INTO "public"."mst_city" VALUES (3, 'putrid meanwhile', 'procedure white unequaled', 'aw while how', NULL);
INSERT INTO "public"."mst_city" VALUES (4, 'viciously', 'far-flung psst stale', 'puny why', NULL);
INSERT INTO "public"."mst_city" VALUES (5, 'so valiantly', 'during carrier', 'during brook blah', NULL);
INSERT INTO "public"."mst_city" VALUES (6, 'yesterday', 'fortress razor lest', 'farewell', NULL);
INSERT INTO "public"."mst_city" VALUES (7, 'yum meh bunker', 'vicious', 'unwitting', NULL);
INSERT INTO "public"."mst_city" VALUES (8, 'wisely', 'bubble', 'fooey in', NULL);
INSERT INTO "public"."mst_city" VALUES (9, 'jasmine', 'dew but meh', 'athwart vainly', NULL);
INSERT INTO "public"."mst_city" VALUES (10, 'towards', 'when', 'minus', NULL);

-- ----------------------------
-- Table structure for mst_country
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_country";
CREATE TABLE "public"."mst_country" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default",
  "region_id" int8
)
;

-- ----------------------------
-- Records of mst_country
-- ----------------------------
INSERT INTO "public"."mst_country" VALUES (1, 'Algeria', NULL, 'DZ', 2);
INSERT INTO "public"."mst_country" VALUES (2, 'Angola', NULL, 'AO', 2);
INSERT INTO "public"."mst_country" VALUES (3, 'Benin', NULL, 'BJ', 2);
INSERT INTO "public"."mst_country" VALUES (4, 'Botswana', NULL, 'BW', 2);
INSERT INTO "public"."mst_country" VALUES (5, 'Burkina Faso', NULL, 'BF', 2);
INSERT INTO "public"."mst_country" VALUES (6, 'Burundi', NULL, 'BI', 2);
INSERT INTO "public"."mst_country" VALUES (7, 'Cabo Verde', NULL, 'CV', 2);
INSERT INTO "public"."mst_country" VALUES (8, 'Cameroon', NULL, 'CM', 2);
INSERT INTO "public"."mst_country" VALUES (9, 'Central African Republic', NULL, 'CF', 2);
INSERT INTO "public"."mst_country" VALUES (10, 'Chad', NULL, 'TD', 2);
INSERT INTO "public"."mst_country" VALUES (11, 'Comoros', NULL, 'KM', 2);
INSERT INTO "public"."mst_country" VALUES (12, 'Congo (Congo-Brazzaville)', NULL, 'CG', 2);
INSERT INTO "public"."mst_country" VALUES (13, 'Côte d''Ivoire', NULL, 'CI', 2);
INSERT INTO "public"."mst_country" VALUES (14, 'Djibouti', NULL, 'DJ', 2);
INSERT INTO "public"."mst_country" VALUES (15, 'Egypt', NULL, 'EG', 2);
INSERT INTO "public"."mst_country" VALUES (16, 'Equatorial Guinea', NULL, 'GQ', 2);
INSERT INTO "public"."mst_country" VALUES (17, 'Eritrea', NULL, 'ER', 2);
INSERT INTO "public"."mst_country" VALUES (18, 'Eswatini', NULL, 'SZ', 2);
INSERT INTO "public"."mst_country" VALUES (19, 'Ethiopia', NULL, 'ET', 2);
INSERT INTO "public"."mst_country" VALUES (20, 'Gabon', NULL, 'GA', 2);
INSERT INTO "public"."mst_country" VALUES (21, 'Gambia', NULL, 'GM', 2);
INSERT INTO "public"."mst_country" VALUES (22, 'Ghana', NULL, 'GH', 2);
INSERT INTO "public"."mst_country" VALUES (23, 'Guinea', NULL, 'GN', 2);
INSERT INTO "public"."mst_country" VALUES (24, 'Guinea-Bissau', NULL, 'GW', 2);
INSERT INTO "public"."mst_country" VALUES (25, 'Kenya', NULL, 'KE', 2);
INSERT INTO "public"."mst_country" VALUES (26, 'Lesotho', NULL, 'LS', 2);
INSERT INTO "public"."mst_country" VALUES (27, 'Liberia', NULL, 'LR', 2);
INSERT INTO "public"."mst_country" VALUES (28, 'Libya', NULL, 'LY', 2);
INSERT INTO "public"."mst_country" VALUES (29, 'Madagascar', NULL, 'MG', 2);
INSERT INTO "public"."mst_country" VALUES (30, 'Malawi', NULL, 'MW', 2);
INSERT INTO "public"."mst_country" VALUES (31, 'Mali', NULL, 'ML', 2);
INSERT INTO "public"."mst_country" VALUES (32, 'Mauritania', NULL, 'MR', 2);
INSERT INTO "public"."mst_country" VALUES (33, 'Mauritius', NULL, 'MU', 2);
INSERT INTO "public"."mst_country" VALUES (34, 'Morocco', NULL, 'MA', 2);
INSERT INTO "public"."mst_country" VALUES (35, 'Mozambique', NULL, 'MZ', 2);
INSERT INTO "public"."mst_country" VALUES (36, 'Namibia', NULL, 'NA', 2);
INSERT INTO "public"."mst_country" VALUES (37, 'Niger', NULL, 'NE', 2);
INSERT INTO "public"."mst_country" VALUES (38, 'Nigeria', NULL, 'NG', 2);
INSERT INTO "public"."mst_country" VALUES (39, 'Rwanda', NULL, 'RW', 2);
INSERT INTO "public"."mst_country" VALUES (40, 'Sao Tome and Principe', NULL, 'ST', 2);
INSERT INTO "public"."mst_country" VALUES (41, 'Senegal', NULL, 'SN', 2);
INSERT INTO "public"."mst_country" VALUES (42, 'Seychelles', NULL, 'SC', 2);
INSERT INTO "public"."mst_country" VALUES (43, 'Sierra Leone', NULL, 'SL', 2);
INSERT INTO "public"."mst_country" VALUES (44, 'Somalia', NULL, 'SO', 2);
INSERT INTO "public"."mst_country" VALUES (45, 'South Africa', NULL, 'ZA', 2);
INSERT INTO "public"."mst_country" VALUES (46, 'South Sudan', NULL, 'SS', 2);
INSERT INTO "public"."mst_country" VALUES (47, 'Sudan', NULL, 'SD', 2);
INSERT INTO "public"."mst_country" VALUES (48, 'Tanzania', NULL, 'TZ', 2);
INSERT INTO "public"."mst_country" VALUES (49, 'Togo', NULL, 'TG', 2);
INSERT INTO "public"."mst_country" VALUES (50, 'Tunisia', NULL, 'TN', 2);
INSERT INTO "public"."mst_country" VALUES (51, 'Uganda', NULL, 'UG', 2);
INSERT INTO "public"."mst_country" VALUES (52, 'Zambia', NULL, 'ZM', 2);
INSERT INTO "public"."mst_country" VALUES (53, 'Zimbabwe', NULL, 'ZW', 2);
INSERT INTO "public"."mst_country" VALUES (54, 'Argentina', NULL, 'AR', 9);
INSERT INTO "public"."mst_country" VALUES (55, 'Bahamas', NULL, 'BS', 9);
INSERT INTO "public"."mst_country" VALUES (56, 'Barbados', NULL, 'BB', 9);
INSERT INTO "public"."mst_country" VALUES (57, 'Belize', NULL, 'BZ', 9);
INSERT INTO "public"."mst_country" VALUES (58, 'Bolivia', NULL, 'BO', 9);
INSERT INTO "public"."mst_country" VALUES (59, 'Brazil', NULL, 'BR', 9);
INSERT INTO "public"."mst_country" VALUES (60, 'Canada', NULL, 'CA', 9);
INSERT INTO "public"."mst_country" VALUES (61, 'Chile', NULL, 'CL', 9);
INSERT INTO "public"."mst_country" VALUES (62, 'Colombia', NULL, 'CO', 9);
INSERT INTO "public"."mst_country" VALUES (63, 'Costa Rica', NULL, 'CR', 9);
INSERT INTO "public"."mst_country" VALUES (64, 'Cuba', NULL, 'CU', 9);
INSERT INTO "public"."mst_country" VALUES (65, 'Dominica', NULL, 'DM', 9);
INSERT INTO "public"."mst_country" VALUES (66, 'Dominican Republic', NULL, 'DO', 9);
INSERT INTO "public"."mst_country" VALUES (67, 'Ecuador', NULL, 'EC', 9);
INSERT INTO "public"."mst_country" VALUES (68, 'El Salvador', NULL, 'SV', 9);
INSERT INTO "public"."mst_country" VALUES (69, 'Grenada', NULL, 'GD', 9);
INSERT INTO "public"."mst_country" VALUES (70, 'Guatemala', NULL, 'GT', 9);
INSERT INTO "public"."mst_country" VALUES (71, 'Guyana', NULL, 'GY', 9);
INSERT INTO "public"."mst_country" VALUES (72, 'Haiti', NULL, 'HT', 9);
INSERT INTO "public"."mst_country" VALUES (73, 'Honduras', NULL, 'HN', 9);
INSERT INTO "public"."mst_country" VALUES (74, 'Jamaica', NULL, 'JM', 9);
INSERT INTO "public"."mst_country" VALUES (75, 'Mexico', NULL, 'MX', 9);
INSERT INTO "public"."mst_country" VALUES (76, 'Nicaragua', NULL, 'NI', 9);
INSERT INTO "public"."mst_country" VALUES (77, 'Panama', NULL, 'PA', 9);
INSERT INTO "public"."mst_country" VALUES (78, 'Paraguay', NULL, 'PY', 9);
INSERT INTO "public"."mst_country" VALUES (79, 'Peru', NULL, 'PE', 9);
INSERT INTO "public"."mst_country" VALUES (80, 'Saint Kitts and Nevis', NULL, 'KN', 9);
INSERT INTO "public"."mst_country" VALUES (81, 'Saint Lucia', NULL, 'LC', 9);
INSERT INTO "public"."mst_country" VALUES (82, 'Saint Vincent and the Grenadines', NULL, 'VC', 9);
INSERT INTO "public"."mst_country" VALUES (83, 'Suriname', NULL, 'SR', 9);
INSERT INTO "public"."mst_country" VALUES (84, 'Trinidad and Tobago', NULL, 'TT', 9);
INSERT INTO "public"."mst_country" VALUES (85, 'United States of America', NULL, 'US', 9);
INSERT INTO "public"."mst_country" VALUES (86, 'Uruguay', NULL, 'UY', 9);
INSERT INTO "public"."mst_country" VALUES (87, 'Venezuela', NULL, 'VE', 9);

-- ----------------------------
-- Table structure for mst_department
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_department";
CREATE TABLE "public"."mst_department" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "department_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "location_id" int8
)
;

-- ----------------------------
-- Records of mst_department
-- ----------------------------
INSERT INTO "public"."mst_department" VALUES (1, 'induct toward', NULL);
INSERT INTO "public"."mst_department" VALUES (2, 'smart', NULL);
INSERT INTO "public"."mst_department" VALUES (3, 'rake yowza show-stopper', NULL);
INSERT INTO "public"."mst_department" VALUES (4, 'outperform liar mortally', NULL);
INSERT INTO "public"."mst_department" VALUES (5, 'apud ejector since', NULL);
INSERT INTO "public"."mst_department" VALUES (6, 'glee personnel', NULL);
INSERT INTO "public"."mst_department" VALUES (7, 'yearly extirpate because', NULL);
INSERT INTO "public"."mst_department" VALUES (8, 'moody sans', NULL);
INSERT INTO "public"."mst_department" VALUES (9, 'apud', NULL);
INSERT INTO "public"."mst_department" VALUES (10, 'impartial frankly bleak', NULL);

-- ----------------------------
-- Table structure for mst_district
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_district";
CREATE TABLE "public"."mst_district" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default",
  "city_id" int8
)
;

-- ----------------------------
-- Records of mst_district
-- ----------------------------
INSERT INTO "public"."mst_district" VALUES (1, 'inside into', 'about', 'equatorial unexpectedly', NULL);
INSERT INTO "public"."mst_district" VALUES (2, 'sparse uh-huh plumb', 'inside geez develop', 'following barring', NULL);
INSERT INTO "public"."mst_district" VALUES (3, 'upward astonish since', 'standard physically', 'rifle', NULL);
INSERT INTO "public"."mst_district" VALUES (4, 'across humming', 'excepting but', 'polished', NULL);
INSERT INTO "public"."mst_district" VALUES (5, 'meanwhile ack', 'scarily formal gosh', 'hail travel helpfully', NULL);
INSERT INTO "public"."mst_district" VALUES (6, 'before louse or', 'and carefully for', 'and extra-large', NULL);
INSERT INTO "public"."mst_district" VALUES (7, 'jiggle thread styling', 'wetly of fav', 'disinter searchingly harrow', NULL);
INSERT INTO "public"."mst_district" VALUES (8, 'quirkily reverse', 'worldly qua beast', 'reveal excepting like', NULL);
INSERT INTO "public"."mst_district" VALUES (9, 'why considering', 'qua querulous eyelashes', 'wilted conventional indeed', NULL);
INSERT INTO "public"."mst_district" VALUES (10, 'fooey', 'geez', 'if however', NULL);

-- ----------------------------
-- Table structure for mst_employee
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_employee";
CREATE TABLE "public"."mst_employee" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "first_name" varchar(255) COLLATE "pg_catalog"."default",
  "last_name" varchar(255) COLLATE "pg_catalog"."default",
  "email" varchar(255) COLLATE "pg_catalog"."default",
  "phone_number" varchar(255) COLLATE "pg_catalog"."default",
  "hire_date" timestamp(6),
  "salary" int8,
  "commission_pct" int8,
  "manager_id" int8,
  "department_id" int8
)
;

-- ----------------------------
-- Records of mst_employee
-- ----------------------------
INSERT INTO "public"."mst_employee" VALUES (1, 'Martine', 'Prohaska', 'Rory95@yahoo.com', 'invasion yowza therapist', '2024-09-24 08:13:10', 22591, 32482, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (2, 'Malachi', 'White', 'Kellie63@yahoo.com', 'but', '2024-09-23 14:02:11', 28787, 18775, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (3, 'Dorthy', 'Douglas', 'Oran.Rath-Zulauf@gmail.com', 'properly inasmuch', '2024-09-23 16:19:05', 19911, 16651, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (4, 'Miracle', 'Bruen', 'Gilbert20@gmail.com', 'jealously', '2024-09-24 04:50:23', 12216, 16691, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (5, 'Eliseo', 'Konopelski', 'Justina.Bauch@gmail.com', 'speedily militarize fortunately', '2024-09-23 15:50:17', 9937, 22751, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (6, 'Javon', 'Miller', 'Edwin.Kunde35@hotmail.com', 'walking adrenaline mmm', '2024-09-23 16:18:09', 24651, 2389, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (7, 'Hubert', 'Wiza', 'Kieran_Brekke-Schiller31@hotmail.com', 'fooey', '2024-09-24 07:51:32', 1717, 23604, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (8, 'Leonor', 'Ferry', 'Gideon38@yahoo.com', 'circa', '2024-09-23 15:12:04', 28752, 11867, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (9, 'Blanche', 'Kuhic', 'Germaine_Tremblay54@yahoo.com', 'scaly where', '2024-09-23 18:44:34', 13339, 5235, NULL, NULL);
INSERT INTO "public"."mst_employee" VALUES (10, 'Emanuel', 'White', 'Keyon.Kihn19@hotmail.com', 'ripen scarce meh', '2024-09-23 12:18:25', 10236, 1387, NULL, NULL);

-- ----------------------------
-- Table structure for mst_job
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_job";
CREATE TABLE "public"."mst_job" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "job_title" varchar(255) COLLATE "pg_catalog"."default",
  "min_salary" int8,
  "max_salary" int8,
  "employee_id" int8
)
;

-- ----------------------------
-- Records of mst_job
-- ----------------------------
INSERT INTO "public"."mst_job" VALUES (1, 'Lead Markets Facilitator', 19227, 16877, NULL);
INSERT INTO "public"."mst_job" VALUES (2, 'Forward Tactics Assistant', 8398, 3772, NULL);
INSERT INTO "public"."mst_job" VALUES (3, 'Regional Infrastructure Designer', 25392, 6625, NULL);
INSERT INTO "public"."mst_job" VALUES (4, 'Regional Data Consultant', 31172, 5332, NULL);
INSERT INTO "public"."mst_job" VALUES (5, 'Investor Creative Facilitator', 440, 28358, NULL);
INSERT INTO "public"."mst_job" VALUES (6, 'Human Configuration Consultant', 16702, 2455, NULL);
INSERT INTO "public"."mst_job" VALUES (7, 'Senior Directives Designer', 15147, 23820, NULL);
INSERT INTO "public"."mst_job" VALUES (8, 'International Data Supervisor', 12977, 20964, NULL);
INSERT INTO "public"."mst_job" VALUES (9, 'Lead Marketing Developer', 12524, 29772, NULL);
INSERT INTO "public"."mst_job" VALUES (10, 'Global Creative Director', 8114, 24628, NULL);

-- ----------------------------
-- Table structure for mst_postal_code
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_postal_code";
CREATE TABLE "public"."mst_postal_code" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "sub_district_id" int8
)
;

-- ----------------------------
-- Records of mst_postal_code
-- ----------------------------
INSERT INTO "public"."mst_postal_code" VALUES (1, 'hence yet', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (2, 'ew sour', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (3, 'honored', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (4, 'but', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (5, 'drat', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (6, 'classroom as phew', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (7, 'among page', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (8, 'so vice', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (9, 'legitimise', NULL);
INSERT INTO "public"."mst_postal_code" VALUES (10, 'heavy', NULL);

-- ----------------------------
-- Table structure for mst_province
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_province";
CREATE TABLE "public"."mst_province" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default",
  "country_id" int8
)
;

-- ----------------------------
-- Records of mst_province
-- ----------------------------
INSERT INTO "public"."mst_province" VALUES (1, 'urgently acidly', 'yuck plan boastfully', 'ruckus shameful eek', NULL);
INSERT INTO "public"."mst_province" VALUES (2, 'pigeonhole consequently honestly', 'mid besides wise', 'on', NULL);
INSERT INTO "public"."mst_province" VALUES (3, 'stealth', 'fooey', 'past ew', NULL);
INSERT INTO "public"."mst_province" VALUES (4, 'against nervously', 'pale', 'daintily', NULL);
INSERT INTO "public"."mst_province" VALUES (5, 'knowledgeably', 'unless speedily godparent', 'extremely upright exemplary', NULL);
INSERT INTO "public"."mst_province" VALUES (6, 'for glorious busily', 'beam subconscious separately', 'phew', NULL);
INSERT INTO "public"."mst_province" VALUES (7, 'cheery educated aha', 'burst centurion', 'pessimistic', NULL);
INSERT INTO "public"."mst_province" VALUES (8, 'drat however neatly', 'bandage irresponsible hm', 'neuter bah as', NULL);
INSERT INTO "public"."mst_province" VALUES (9, 'soon especially', 'aboard', 'pollutant until systematise', NULL);
INSERT INTO "public"."mst_province" VALUES (10, 'drag', 'forgery anesthesiologist', 'till ask', NULL);

-- ----------------------------
-- Table structure for mst_region
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_region";
CREATE TABLE "public"."mst_region" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of mst_region
-- ----------------------------
INSERT INTO "public"."mst_region" VALUES (1, 'declassify satirise doorpost', 'parcel the', 'correspondent');
INSERT INTO "public"."mst_region" VALUES (2, 'pig', 'suspiciously like', 'mid lest though');
INSERT INTO "public"."mst_region" VALUES (3, 'although evil whereas', 'fiercely abaft elderly', 'cruelly');
INSERT INTO "public"."mst_region" VALUES (4, 'brr willfully garnish', 'like rhapsodise', 'ouch substantial');
INSERT INTO "public"."mst_region" VALUES (5, 'although bleakly', 'near', 'to gripping');
INSERT INTO "public"."mst_region" VALUES (6, 'nuke following', 'elated', 'previous gaseous affectionate');
INSERT INTO "public"."mst_region" VALUES (7, 'behind goose', 'blah an psst', 'astride');
INSERT INTO "public"."mst_region" VALUES (8, 'especially', 'raven', 'times ouch');
INSERT INTO "public"."mst_region" VALUES (9, 'primary ah debris', 'following', 'hm marionberry enfranchise');
INSERT INTO "public"."mst_region" VALUES (10, 'ack', 'agile', 'meanwhile verbally laptop');

-- ----------------------------
-- Table structure for mst_service
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_service";
CREATE TABLE "public"."mst_service" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "price" numeric(21,2),
  "duration_in_hours" int4,
  "service_type" varchar(255) COLLATE "pg_catalog"."default",
  "testimonial_id" int8
)
;

-- ----------------------------
-- Records of mst_service
-- ----------------------------
INSERT INTO "public"."mst_service" VALUES (1, 'in gentle', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1222.13, 32048, 'TICKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (2, 'frugal shortwave', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1955.73, 16093, 'TICKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (3, 'ugh', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 3024.84, 4710, 'TICKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (4, 'standard briskly meh', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 28428.26, 16956, 'EVENT_MANAGEMENT', NULL);
INSERT INTO "public"."mst_service" VALUES (5, 'apparatus blossom businessman', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 18161.32, 30179, 'MARKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (6, 'woot readily', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 24916.76, 12722, 'TICKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (7, 'yippee', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 26956.81, 15524, 'SPONSORSHIP', NULL);
INSERT INTO "public"."mst_service" VALUES (8, 'quietly so', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 4563.25, 9956, 'SPONSORSHIP', NULL);
INSERT INTO "public"."mst_service" VALUES (9, 'phew', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 2785.69, 26317, 'TICKETING', NULL);
INSERT INTO "public"."mst_service" VALUES (10, 'capability per', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 625.55, 17361, 'MARKETING', NULL);

-- ----------------------------
-- Table structure for mst_sub_district
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_sub_district";
CREATE TABLE "public"."mst_sub_district" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unm_49_code" varchar(255) COLLATE "pg_catalog"."default",
  "iso_alpha_2_code" varchar(255) COLLATE "pg_catalog"."default",
  "district_id" int8
)
;

-- ----------------------------
-- Records of mst_sub_district
-- ----------------------------
INSERT INTO "public"."mst_sub_district" VALUES (1, 'ha lighting clutch', 'over thoroughly ew', 'hence since aha', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (2, 'deep', 'pipe but', 'teeming revert', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (3, 'pfft photocopy', 'versus on kitsch', 'unsteady', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (4, 'frenetically', 'behind yet swab', 'jail less curler', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (5, 'free', 'sans', 'modulo responsible dazzling', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (6, 'until amid cruelly', 'instead', 'meh via behavior', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (7, 'oddly', 'provided far-off disinhibit', 'rare within along', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (8, 'whispered below', 'inside', 'prattle', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (9, 'hastily', 'open', 'slow', NULL);
INSERT INTO "public"."mst_sub_district" VALUES (10, 'zowie fortunately', 'swordfight raise', 'playfully steel why', NULL);

-- ----------------------------
-- Table structure for mst_task
-- ----------------------------
DROP TABLE IF EXISTS "public"."mst_task";
CREATE TABLE "public"."mst_task" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "title" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of mst_task
-- ----------------------------
INSERT INTO "public"."mst_task" VALUES (1, 'perky pish which', 'leader warn after');
INSERT INTO "public"."mst_task" VALUES (2, 'realistic nation', 'while vine peek');
INSERT INTO "public"."mst_task" VALUES (3, 'underwire mad', 'unlike enlarge aw');
INSERT INTO "public"."mst_task" VALUES (4, 'inside', 'impair suborn scoot');
INSERT INTO "public"."mst_task" VALUES (5, 'huzzah south', 'qua whenever thorny');
INSERT INTO "public"."mst_task" VALUES (6, 'knottily gosh atheist', 'serenade amidst');
INSERT INTO "public"."mst_task" VALUES (7, 'damaged furthermore', 'whoa psst who');
INSERT INTO "public"."mst_task" VALUES (8, 'yowza', 'ample policy perfumed');
INSERT INTO "public"."mst_task" VALUES (9, 'sweetly instead because', 'now but resemble');
INSERT INTO "public"."mst_task" VALUES (10, 'progenitor astride', 'ah possible than');

-- ----------------------------
-- Table structure for rel_mst_job__task
-- ----------------------------
DROP TABLE IF EXISTS "public"."rel_mst_job__task";
CREATE TABLE "public"."rel_mst_job__task" (
  "task_id" int8 NOT NULL,
  "mst_job_id" int8 NOT NULL
)
;

-- ----------------------------
-- Records of rel_mst_job__task
-- ----------------------------

-- ----------------------------
-- Table structure for trx_event
-- ----------------------------
DROP TABLE IF EXISTS "public"."trx_event";
CREATE TABLE "public"."trx_event" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "date" timestamp(6) NOT NULL,
  "location" varchar(255) COLLATE "pg_catalog"."default",
  "capacity" int4,
  "price" numeric(21,2),
  "status" varchar(255) COLLATE "pg_catalog"."default",
  "service_id" int8,
  "testimonial_id" int8
)
;

-- ----------------------------
-- Records of trx_event
-- ----------------------------
INSERT INTO "public"."trx_event" VALUES (1, 'duh tram', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-24 05:40:10', 'rarely although', 2651, 25469.71, 'ONGOING', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (2, 'puma', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-23 20:12:10', 'dreamily giant down', 15260, 28823.56, 'UPCOMING', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (3, 'thorough eek fooey', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-23 13:35:48', 'greatly wherever', 14695, 22260.52, 'ONGOING', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (4, 'predispose um', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-24 09:06:54', 'inborn', 5796, 16814.29, 'CANCELLED', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (5, 'though bashfully', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-24 02:13:29', 'innocently amidst', 21542, 12153.74, 'COMPLETED', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (6, 'sectional', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-24 05:49:26', 'boast', 16085, 30041.27, 'UPCOMING', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (7, 'summons', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-23 14:25:20', 'pink abnormally', 21037, 17676.44, 'CANCELLED', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (8, 'unpleasant', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-23 15:34:24', 'trusty conspiracy', 24660, 7252.98, 'COMPLETED', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (9, 'productive cell', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-24 08:23:29', 'illustrious carpet', 26818, 4212.96, 'ONGOING', NULL, NULL);
INSERT INTO "public"."trx_event" VALUES (10, 'meanwhile vibrissae', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', '2024-09-23 21:47:58', 'fearless unruly which', 18186, 1240.77, 'ONGOING', NULL, NULL);

-- ----------------------------
-- Table structure for trx_testimonial
-- ----------------------------
DROP TABLE IF EXISTS "public"."trx_testimonial";
CREATE TABLE "public"."trx_testimonial" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "feedback" text COLLATE "pg_catalog"."default" NOT NULL,
  "rating" int4 NOT NULL,
  "date" timestamp(6) NOT NULL
)
;

-- ----------------------------
-- Records of trx_testimonial
-- ----------------------------
INSERT INTO "public"."trx_testimonial" VALUES (1, 'rehash lively among', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 5, '2024-09-23 10:44:29');
INSERT INTO "public"."trx_testimonial" VALUES (2, 'industrialise', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1, '2024-09-24 02:51:52');
INSERT INTO "public"."trx_testimonial" VALUES (3, 'commonly snuck bah', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 2, '2024-09-23 10:03:45');
INSERT INTO "public"."trx_testimonial" VALUES (4, 'whether vivaciously alongside', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 5, '2024-09-23 23:22:19');
INSERT INTO "public"."trx_testimonial" VALUES (5, 'elegantly hop', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1, '2024-09-24 07:08:01');
INSERT INTO "public"."trx_testimonial" VALUES (6, 'pro gosh', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 2, '2024-09-23 09:57:11');
INSERT INTO "public"."trx_testimonial" VALUES (7, 'however categorise giant', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1, '2024-09-23 10:52:31');
INSERT INTO "public"."trx_testimonial" VALUES (8, 'probable', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 5, '2024-09-24 03:49:30');
INSERT INTO "public"."trx_testimonial" VALUES (9, 'misconceive contention', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 4, '2024-09-23 23:46:36');
INSERT INTO "public"."trx_testimonial" VALUES (10, 'dearly minus above', 'JHipster is a development platform to generate, develop and deploy Spring Boot + Angular / React / Vue Web applications and Spring microservices.', 1, '2024-09-24 03:57:13');

-- ----------------------------
-- Table structure for trx_tournament
-- ----------------------------
DROP TABLE IF EXISTS "public"."trx_tournament";
CREATE TABLE "public"."trx_tournament" (
  "id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1500
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "prize_amount" numeric(21,2),
  "start_date" timestamp(6) NOT NULL,
  "end_date" timestamp(6) NOT NULL,
  "location" varchar(255) COLLATE "pg_catalog"."default",
  "max_participants" int4,
  "status" varchar(255) COLLATE "pg_catalog"."default",
  "event_id" int8
)
;

-- ----------------------------
-- Records of trx_tournament
-- ----------------------------
INSERT INTO "public"."trx_tournament" VALUES (1, 'anenst', 'SOLO', 7090.02, '2024-09-23 15:01:27', '2024-09-23 11:11:01', 'bully which', 21895, 'CANCELLED', NULL);
INSERT INTO "public"."trx_tournament" VALUES (2, 'assured afore', 'SOLO', 19539.51, '2024-09-24 00:03:42', '2024-09-23 19:12:52', 'so', 61, 'IN_PROGRESS', NULL);
INSERT INTO "public"."trx_tournament" VALUES (3, 'of mmm corner', 'TEAM', 647.02, '2024-09-24 05:50:04', '2024-09-24 01:23:11', 'promptly unless sleep', 23633, 'FINISHED', NULL);
INSERT INTO "public"."trx_tournament" VALUES (4, 'pawn minus', 'TEAM', 17268.57, '2024-09-23 16:23:38', '2024-09-24 00:15:28', 'yowza step-aunt following', 19951, 'CANCELLED', NULL);
INSERT INTO "public"."trx_tournament" VALUES (5, 'grounded pfft', 'TEAM', 8086.95, '2024-09-24 01:29:53', '2024-09-23 23:26:15', 'angora promote', 14717, 'UPCOMING', NULL);
INSERT INTO "public"."trx_tournament" VALUES (6, 'quizzical so', 'TEAM', 25656.04, '2024-09-23 15:26:16', '2024-09-24 02:00:03', 'ninja jaded', 10893, 'UPCOMING', NULL);
INSERT INTO "public"."trx_tournament" VALUES (7, 'whereas provided consequently', 'TEAM', 11939.15, '2024-09-23 12:13:32', '2024-09-24 07:55:19', 'fortify alongside', 13603, 'CANCELLED', NULL);
INSERT INTO "public"."trx_tournament" VALUES (8, 'loosely', 'TEAM', 27669.52, '2024-09-24 07:49:44', '2024-09-24 01:30:04', 'nifty flame negligible', 8737, 'UPCOMING', NULL);
INSERT INTO "public"."trx_tournament" VALUES (9, 'into uh-huh', 'SOLO', 6343.50, '2024-09-23 11:31:06', '2024-09-24 02:47:32', 'rigidly incidentally carrier', 19819, 'FINISHED', NULL);
INSERT INTO "public"."trx_tournament" VALUES (10, 'meh', 'TEAM', 22496.13, '2024-09-23 09:44:38', '2024-09-23 22:47:11', 'since briefly', 21986, 'IN_PROGRESS', NULL);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."jhi_user_id_seq"
OWNED BY "public"."jhi_user"."id";
SELECT setval('"public"."jhi_user_id_seq"', 1050, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."job_history_id_seq"
OWNED BY "public"."job_history"."id";
SELECT setval('"public"."job_history_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."location_id_seq"
OWNED BY "public"."location"."id";
SELECT setval('"public"."location_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_city_id_seq"
OWNED BY "public"."mst_city"."id";
SELECT setval('"public"."mst_city_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_country_id_seq"
OWNED BY "public"."mst_country"."id";
SELECT setval('"public"."mst_country_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_department_id_seq"
OWNED BY "public"."mst_department"."id";
SELECT setval('"public"."mst_department_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_district_id_seq"
OWNED BY "public"."mst_district"."id";
SELECT setval('"public"."mst_district_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_employee_id_seq"
OWNED BY "public"."mst_employee"."id";
SELECT setval('"public"."mst_employee_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_job_id_seq"
OWNED BY "public"."mst_job"."id";
SELECT setval('"public"."mst_job_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_postal_code_id_seq"
OWNED BY "public"."mst_postal_code"."id";
SELECT setval('"public"."mst_postal_code_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_province_id_seq"
OWNED BY "public"."mst_province"."id";
SELECT setval('"public"."mst_province_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_region_id_seq"
OWNED BY "public"."mst_region"."id";
SELECT setval('"public"."mst_region_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_service_id_seq"
OWNED BY "public"."mst_service"."id";
SELECT setval('"public"."mst_service_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_sub_district_id_seq"
OWNED BY "public"."mst_sub_district"."id";
SELECT setval('"public"."mst_sub_district_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mst_task_id_seq"
OWNED BY "public"."mst_task"."id";
SELECT setval('"public"."mst_task_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."trx_event_id_seq"
OWNED BY "public"."trx_event"."id";
SELECT setval('"public"."trx_event_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."trx_testimonial_id_seq"
OWNED BY "public"."trx_testimonial"."id";
SELECT setval('"public"."trx_testimonial_id_seq"', 1500, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."trx_tournament_id_seq"
OWNED BY "public"."trx_tournament"."id";
SELECT setval('"public"."trx_tournament_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table databasechangeloglock
-- ----------------------------
ALTER TABLE "public"."databasechangeloglock" ADD CONSTRAINT "databasechangeloglock_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table jhi_authority
-- ----------------------------
ALTER TABLE "public"."jhi_authority" ADD CONSTRAINT "jhi_authority_pkey" PRIMARY KEY ("name");

-- ----------------------------
-- Auto increment value for jhi_user
-- ----------------------------
SELECT setval('"public"."jhi_user_id_seq"', 1050, false);

-- ----------------------------
-- Uniques structure for table jhi_user
-- ----------------------------
ALTER TABLE "public"."jhi_user" ADD CONSTRAINT "ux_user_login" UNIQUE ("login");
ALTER TABLE "public"."jhi_user" ADD CONSTRAINT "ux_user_email" UNIQUE ("email");

-- ----------------------------
-- Primary Key structure for table jhi_user
-- ----------------------------
ALTER TABLE "public"."jhi_user" ADD CONSTRAINT "jhi_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table jhi_user_authority
-- ----------------------------
ALTER TABLE "public"."jhi_user_authority" ADD CONSTRAINT "jhi_user_authority_pkey" PRIMARY KEY ("user_id", "authority_name");

-- ----------------------------
-- Auto increment value for job_history
-- ----------------------------
SELECT setval('"public"."job_history_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table job_history
-- ----------------------------
ALTER TABLE "public"."job_history" ADD CONSTRAINT "ux_job_history__job_id" UNIQUE ("job_id");
ALTER TABLE "public"."job_history" ADD CONSTRAINT "ux_job_history__department_id" UNIQUE ("department_id");
ALTER TABLE "public"."job_history" ADD CONSTRAINT "ux_job_history__employee_id" UNIQUE ("employee_id");

-- ----------------------------
-- Primary Key structure for table job_history
-- ----------------------------
ALTER TABLE "public"."job_history" ADD CONSTRAINT "job_history_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for location
-- ----------------------------
SELECT setval('"public"."location_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table location
-- ----------------------------
ALTER TABLE "public"."location" ADD CONSTRAINT "location_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_city
-- ----------------------------
SELECT setval('"public"."mst_city_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_city
-- ----------------------------
ALTER TABLE "public"."mst_city" ADD CONSTRAINT "ux_mst_city__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_city" ADD CONSTRAINT "ux_mst_city__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_city
-- ----------------------------
ALTER TABLE "public"."mst_city" ADD CONSTRAINT "mst_city_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_country
-- ----------------------------
SELECT setval('"public"."mst_country_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_country
-- ----------------------------
ALTER TABLE "public"."mst_country" ADD CONSTRAINT "ux_mst_country__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_country" ADD CONSTRAINT "ux_mst_country__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_country
-- ----------------------------
ALTER TABLE "public"."mst_country" ADD CONSTRAINT "mst_country_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_department
-- ----------------------------
SELECT setval('"public"."mst_department_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_department
-- ----------------------------
ALTER TABLE "public"."mst_department" ADD CONSTRAINT "ux_mst_department__location_id" UNIQUE ("location_id");

-- ----------------------------
-- Primary Key structure for table mst_department
-- ----------------------------
ALTER TABLE "public"."mst_department" ADD CONSTRAINT "mst_department_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_district
-- ----------------------------
SELECT setval('"public"."mst_district_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_district
-- ----------------------------
ALTER TABLE "public"."mst_district" ADD CONSTRAINT "ux_mst_district__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_district" ADD CONSTRAINT "ux_mst_district__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_district
-- ----------------------------
ALTER TABLE "public"."mst_district" ADD CONSTRAINT "mst_district_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_employee
-- ----------------------------
SELECT setval('"public"."mst_employee_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table mst_employee
-- ----------------------------
ALTER TABLE "public"."mst_employee" ADD CONSTRAINT "mst_employee_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_job
-- ----------------------------
SELECT setval('"public"."mst_job_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table mst_job
-- ----------------------------
ALTER TABLE "public"."mst_job" ADD CONSTRAINT "mst_job_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_postal_code
-- ----------------------------
SELECT setval('"public"."mst_postal_code_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_postal_code
-- ----------------------------
ALTER TABLE "public"."mst_postal_code" ADD CONSTRAINT "ux_mst_postal_code__code" UNIQUE ("code");

-- ----------------------------
-- Primary Key structure for table mst_postal_code
-- ----------------------------
ALTER TABLE "public"."mst_postal_code" ADD CONSTRAINT "mst_postal_code_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_province
-- ----------------------------
SELECT setval('"public"."mst_province_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_province
-- ----------------------------
ALTER TABLE "public"."mst_province" ADD CONSTRAINT "ux_mst_province__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_province" ADD CONSTRAINT "ux_mst_province__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_province
-- ----------------------------
ALTER TABLE "public"."mst_province" ADD CONSTRAINT "mst_province_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_region
-- ----------------------------
SELECT setval('"public"."mst_region_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_region
-- ----------------------------
ALTER TABLE "public"."mst_region" ADD CONSTRAINT "ux_mst_region__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_region" ADD CONSTRAINT "ux_mst_region__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_region
-- ----------------------------
ALTER TABLE "public"."mst_region" ADD CONSTRAINT "mst_region_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_service
-- ----------------------------
SELECT setval('"public"."mst_service_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table mst_service
-- ----------------------------
ALTER TABLE "public"."mst_service" ADD CONSTRAINT "mst_service_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_sub_district
-- ----------------------------
SELECT setval('"public"."mst_sub_district_id_seq"', 1500, false);

-- ----------------------------
-- Uniques structure for table mst_sub_district
-- ----------------------------
ALTER TABLE "public"."mst_sub_district" ADD CONSTRAINT "ux_mst_sub_district__unm_49_code" UNIQUE ("unm_49_code");
ALTER TABLE "public"."mst_sub_district" ADD CONSTRAINT "ux_mst_sub_district__iso_alpha_2_code" UNIQUE ("iso_alpha_2_code");

-- ----------------------------
-- Primary Key structure for table mst_sub_district
-- ----------------------------
ALTER TABLE "public"."mst_sub_district" ADD CONSTRAINT "mst_sub_district_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for mst_task
-- ----------------------------
SELECT setval('"public"."mst_task_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table mst_task
-- ----------------------------
ALTER TABLE "public"."mst_task" ADD CONSTRAINT "mst_task_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rel_mst_job__task
-- ----------------------------
ALTER TABLE "public"."rel_mst_job__task" ADD CONSTRAINT "rel_mst_job__task_pkey" PRIMARY KEY ("mst_job_id", "task_id");

-- ----------------------------
-- Auto increment value for trx_event
-- ----------------------------
SELECT setval('"public"."trx_event_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table trx_event
-- ----------------------------
ALTER TABLE "public"."trx_event" ADD CONSTRAINT "trx_event_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for trx_testimonial
-- ----------------------------
SELECT setval('"public"."trx_testimonial_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table trx_testimonial
-- ----------------------------
ALTER TABLE "public"."trx_testimonial" ADD CONSTRAINT "trx_testimonial_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Auto increment value for trx_tournament
-- ----------------------------
SELECT setval('"public"."trx_tournament_id_seq"', 1500, false);

-- ----------------------------
-- Primary Key structure for table trx_tournament
-- ----------------------------
ALTER TABLE "public"."trx_tournament" ADD CONSTRAINT "trx_tournament_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table jhi_user_authority
-- ----------------------------
ALTER TABLE "public"."jhi_user_authority" ADD CONSTRAINT "fk_authority_name" FOREIGN KEY ("authority_name") REFERENCES "public"."jhi_authority" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."jhi_user_authority" ADD CONSTRAINT "fk_user_id" FOREIGN KEY ("user_id") REFERENCES "public"."jhi_user" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table job_history
-- ----------------------------
ALTER TABLE "public"."job_history" ADD CONSTRAINT "fk_job_history__department_id" FOREIGN KEY ("department_id") REFERENCES "public"."mst_department" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."job_history" ADD CONSTRAINT "fk_job_history__employee_id" FOREIGN KEY ("employee_id") REFERENCES "public"."mst_employee" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."job_history" ADD CONSTRAINT "fk_job_history__job_id" FOREIGN KEY ("job_id") REFERENCES "public"."mst_job" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_city
-- ----------------------------
ALTER TABLE "public"."mst_city" ADD CONSTRAINT "fk_mst_city__province_id" FOREIGN KEY ("province_id") REFERENCES "public"."mst_province" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_country
-- ----------------------------
ALTER TABLE "public"."mst_country" ADD CONSTRAINT "fk_mst_country__region_id" FOREIGN KEY ("region_id") REFERENCES "public"."mst_region" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_department
-- ----------------------------
ALTER TABLE "public"."mst_department" ADD CONSTRAINT "fk_mst_department__location_id" FOREIGN KEY ("location_id") REFERENCES "public"."location" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_district
-- ----------------------------
ALTER TABLE "public"."mst_district" ADD CONSTRAINT "fk_mst_district__city_id" FOREIGN KEY ("city_id") REFERENCES "public"."mst_city" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_employee
-- ----------------------------
ALTER TABLE "public"."mst_employee" ADD CONSTRAINT "fk_mst_employee__department_id" FOREIGN KEY ("department_id") REFERENCES "public"."mst_department" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."mst_employee" ADD CONSTRAINT "fk_mst_employee__manager_id" FOREIGN KEY ("manager_id") REFERENCES "public"."mst_employee" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_job
-- ----------------------------
ALTER TABLE "public"."mst_job" ADD CONSTRAINT "fk_mst_job__employee_id" FOREIGN KEY ("employee_id") REFERENCES "public"."mst_employee" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_postal_code
-- ----------------------------
ALTER TABLE "public"."mst_postal_code" ADD CONSTRAINT "fk_mst_postal_code__sub_district_id" FOREIGN KEY ("sub_district_id") REFERENCES "public"."mst_sub_district" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_province
-- ----------------------------
ALTER TABLE "public"."mst_province" ADD CONSTRAINT "fk_mst_province__country_id" FOREIGN KEY ("country_id") REFERENCES "public"."mst_country" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_service
-- ----------------------------
ALTER TABLE "public"."mst_service" ADD CONSTRAINT "fk_mst_service__testimonial_id" FOREIGN KEY ("testimonial_id") REFERENCES "public"."trx_testimonial" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table mst_sub_district
-- ----------------------------
ALTER TABLE "public"."mst_sub_district" ADD CONSTRAINT "fk_mst_sub_district__district_id" FOREIGN KEY ("district_id") REFERENCES "public"."mst_district" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table rel_mst_job__task
-- ----------------------------
ALTER TABLE "public"."rel_mst_job__task" ADD CONSTRAINT "fk_rel_mst_job__task__mst_job_id" FOREIGN KEY ("mst_job_id") REFERENCES "public"."mst_job" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."rel_mst_job__task" ADD CONSTRAINT "fk_rel_mst_job__task__task_id" FOREIGN KEY ("task_id") REFERENCES "public"."mst_task" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table trx_event
-- ----------------------------
ALTER TABLE "public"."trx_event" ADD CONSTRAINT "fk_trx_event__service_id" FOREIGN KEY ("service_id") REFERENCES "public"."mst_service" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."trx_event" ADD CONSTRAINT "fk_trx_event__testimonial_id" FOREIGN KEY ("testimonial_id") REFERENCES "public"."trx_testimonial" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table trx_tournament
-- ----------------------------
ALTER TABLE "public"."trx_tournament" ADD CONSTRAINT "fk_trx_tournament__event_id" FOREIGN KEY ("event_id") REFERENCES "public"."trx_event" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
