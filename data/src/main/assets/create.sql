create table  IF not exists MTL_INSPECTION_HEADERS
(
  id      VARCHAR2(32)  PRIMARY KEY NOT NULL,
  record_num       TEXT,
  ref_type         TEXT,
  biz_type         TEXT,
  move_type        TEXT,
  po_num           TEXT,
  supplier_num     TEXT,
  supplier_desc    TEXT,
  save_flag        TEXT,
  status           TEXT,
  created_by       TEXT,
  creation_date    TEXT,
  voucher_date     TEXT
);

create table  IF not exists MTL_INSPECTION_LINES
(
  id                    VARCHAR2(32)  PRIMARY KEY  NOT NULL,
  ref_code_id           VARCHAR2(32),
  record_num            TEXT,
  line_num              TEXT,
  arrival_quantity      REAL,
  batch_flag            TEXT,
  inspection_date       TEXT,
  inspection_result     TEXT,
  inv_id                VARCHAR2(32),
  inv_code              TEXT,
  inv_name              TEXT,
  lineInspect_flag      TEXT,
  material_id           TEXT,
  material_num          TEXT,
  material_desc         TEXT,
  material_group        TEXT,
  quantity              REAL,
  order_quantity        REAL,
  photo_flag            TEXT,
  poLine_num            TEXT,
  total_quantity        REAL,
  unit                  TEXT,
  work_id               VARCHAR2(32),
  work_code             TEXT,
  work_name             TEXT
);

create table  IF not exists MTL_TRANSACTION_HEADERS
(
   id               VARCHAR2(32) PRIMARY KEY  NOT NULL,
   ref_code_id      VARCHAR2(32),
   biz_type         TEXT,
   ref_type         TEXT,
   move_type        TEXT,
   voucher_date     TEXT,
   created_by       TEXT,
   created_date     TEXT
);

create table IF not exists MTL_TRANSACTION_LINES
(
  id               VARCHAR2(32) PRIMARY KEY NOT NULL,
  trans_id         VARCHAR2(32),
  ref_line_id      VARCHAR2(32),
  line_num         TEXT,
  inv_id           VARCHAR2(32),
  inv_code         TEXT,
  inv_name         TEXT,
  work_id          VARCHAR2(32),
  work_code        TEXT,
  work_name        TEXT,
  send_inv_id      VARCHAR2(32),
  send_inv_code    TEXT,
  send_inv_name    TEXT,
  send_work_id     VARCHAR2(32),
  send_work_code   TEXT,
  send_work_name   TEXT

);

create table MTL_TRANSACTION_LINES_CW
(
  id               VARCHAR2(32) PRIMARY KEY NOT NULL,
  trans_id         VARCHAR2(32),
  trans_line_id    VARCHAR2(32),
  line_num         TEXT,
  location         TEXT,
  inv_quantity     REAL,
  quantity         REAL,
  created_by       TEXT,
  inv_code         TEXT,
  work_code        TEXT,
  batch_flag       TEXT,
  supplier_id      VARCHAR2(32),
  supplier_num     TEXT
);

create table IF not exists T_LOCAL_DELETE
(
  delete_id       VARCHAR2(32) PRIMARY KEY NOT NULL,
  biz_type        TEXT,
  ref_type        TEXT
);

create table IF not exists T_EXTRA_HEADER
(
  id               VARCHAR2(32) PRIMARY KEY NOT NULL,
  config_type      TEXT
);

create table IF not exists T_EXTRA_LINE
(
   id               VARCHAR2(32) PRIMARY KEY NOT NULL,
   ref_code_id      VARCHAR2(32),
   config_type      TEXT

);

create table IF not exists T_EXTRA_CW
(
 id               VARCHAR2(32) PRIMARY KEY NOT NULL,
 ref_line_id      VARCHAR2(32),
 config_type      TEXT
);

create table IF not exists T_TRANSACTION_EXTRA_HEADER
(
   id               VARCHAR2(32) PRIMARY KEY NOT NULL,
   config_type      TEXT
);


create table IF not exists T_TRANSACTION_EXTRA_LINE
(
   id               VARCHAR2(32) PRIMARY KEY NOT NULL,
   trans_line_id    VARCHAR2(32),
   config_type      TEXT
);

create table IF not exists T_TRANSACTION_EXTRA_CW
(
   id               VARCHAR2(32) PRIMARY KEY NOT NULL,
   trans_loc_id     VARCHAR2(32),
   config_type      TEXT
);

create table MTL_CHECK
(
  id                VARCHAR2(32) PRIMARY KEY NOT NULL,
  trans_id          VARCHAR2(32),
  line_num          TEXT,
  storage_num       TEXT,
  location          TEXT,
  inv_quantity      REAL,
  work_code         TEXT,
  inv_code          TEXT,
  batch_flag        TEXT,
  work_id           TEXT,
  inv_id            TEXT,
  material_id       VARCHAR2(32),
  material_num      TEXT,
  material_desc     TEXT,
  material_unit     TEXT,
  material_group    TEXT,
  quantity          TEXT,
  total_quantity    REAL,
  special_inventory_flag TEXT
);

create table MTL_IMAGES
(
  id            VARCHAR2(32) PRIMARY KEY NOT NULL,
  ref_num       TEXT,
  ref_line_id   VARCHAR2(32),
  image_dir     TEXT,
  image_name    TEXT,
  created_by    TEXT,
  local_flag    TEXT,
  biz_type      TEXT,
  ref_type      TEXT,
  take_photo_type    INTEGER,
  creation_date TEXT
);


create table IF not exists REQUEST_DATE
(
  id          VARCHAR2(32) PRIMARY KEY,
  query_type  TEXT,
  query_date  TEXT
);

create table BASE_COST_CENTER
(
  id                VARCHAR2(32) PRIMARY KEY NOT NULL,
  org_id            VARCHAR2(32),
  cost_center       TEXT,
  cost_center_desc  TEXT,
  func_area         TEXT,
  func_area_desc    TEXT,
  created_by        TEXT,
  creation_date     TEXT,
  last_updated_by   TEXT,
  last_update_date  TEXT,
  sap_creation_date TEXT,
  sap_update_date   TEXT
);


create table  BASE_INSPECTION_PLACE
(
  id               VARCHAR2(32) PRIMARY KEY NOT NULL,
  code             TEXT,
  name             TEXT
);

create table IF not exists BASE_MATERIAL_CODE
(
  id                VARCHAR2(32) PRIMARY KEY NOT NULL,
  material_num      TEXT,
  material_desc     TEXT,
  material_group    TEXT,
  unit              TEXT,
  status            TEXT,
  created_by        TEXT,
  creation_date     TEXT,
  last_updated_by   TEXT,
  last_update_date  TEXT,
  old_material_num  TEXT,
  material_type     TEXT,
  sap_creation_date TEXT,
  sap_update_date   TEXT
);


create table IF not exists BASE_SUPPLIER
(
  id               VARCHAR2(32)  PRIMARY KEY NOT NULL,
  org_id           VARCHAR2(32),
  supplier_code    TEXT,
  supplier_desc    TEXT,
  created_by       TEXT,
  creation_date    TEXT,
  last_updated_by  TEXT,
  last_update_date TEXT
);

create table IF not exists BASE_WAREHOUSE_GROUP
(
  id         VARCHAR2(32) PRIMARY KEY NOT null,
  group_code TEXT,
  group_desc TEXT
);

create table IF not exists BASE_LOCATION
(
  id                VARCHAR2(32) PRIMARY KEY NOT NULL,
  location          TEXT,
  storage_num       TEXT,
  created_by        TEXT,
  creation_date     TEXT,
  last_updated_by   TEXT,
  last_update_date  TEXT,
  sap_update_date   TEXT,
  sap_creation_date TEXT,
  location_type     TEXT
);

create table IF not exists P_AUTH_ORG
(
  org_id       VARCHAR2(32) PRIMARY KEY  NOT NULL,
  org_name     TEXT,
  org_code     TEXT,
  parent_id    VARCHAR2(32),
  order_no     INTEGER,
  phone_number TEXT,
  post_address TEXT,
  memo         TEXT,
  create_date  TEXT,
  creator      TEXT,
  modify_date  TEXT,
  modifier     TEXT,
  org_level    TEXT,
  lock_flag    TEXT,
  storage_code TEXT,
  storage_name TEXT,
  evaluation   TEXT
);

create table IF not exists P_AUTH_ORG2
(
  org_id       VARCHAR2(32) PRIMARY KEY  NOT NULL,
  org_name     TEXT,
  org_code     TEXT,
  parent_id    VARCHAR2(32),
  order_no     INTEGER,
  phone_number TEXT,
  post_address TEXT,
  memo         TEXT,
  create_date  TEXT,
  creator      TEXT,
  modify_date  TEXT,
  modifier     TEXT,
  org_level    TEXT,
  lock_flag    TEXT,
  storage_code TEXT,
  storage_name TEXT,
  evaluation   TEXT
);

create table IF not exists T_USER
(
  login_id           VARCHAR2(32) PRIMARY KEY NOT NULL,
  auth_orgs          TEXT,
  user_id            TEXT,
  last_login_date    INTEGER,
  user_name          TEXT
);

create table IF not exists T_CONFIG
(
    id               VARCHAR2(32) PRIMARY KEY NOT NULL,
    property_name    TEXT,
    property_code    TEXT,
    display_flag     TEXT,
    input_flag       TEXT,
    company_code     TEXT,
    company_name     TEXT,
    module_code      TEXT,
    module_name      TEXT,
    biz_type         TEXT,
    ref_code         TEXT,
    ref_name         TEXT,
    config_type      TEXT,
    ui_type          TEXT,
    col_num          TEXT,
    col_name         TEXT,
    data_source      TEXT
);

create table IF not exists T_EXTRA_DATA_SOURCE
(
    id               VARCHAR2(32) PRIMARY KEY NOT NULL,
    code             TEXT,
    name             TEXT,
    sort             TEXT,
    val              TEXT
);

create table IF not exists T_FRAGMENT_CONFIGS
(
    id               VARCHAR2(32) PRIMARY KEY NOT NULL,
    fragment_tag     TEXT,
    biz_type         TEXT,
    ref_type         TEXT,
    tab_title        TEXT,
    fragment_type    INTEGER,
    class_name       TEXT
);