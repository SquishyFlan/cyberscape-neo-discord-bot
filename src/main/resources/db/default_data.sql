INSERT INTO skill_scale VALUES(11, 41, 6, 5, 1, 1, 10, 1);
INSERT INTO stat_level_scale VALUES
  ('str', 100, 5, 10, 5),
  ('vit', 100, 5, 10, 5),
  ('dex', 100, 5, 10, 5),
  ('int', 100, 5, 10, 5),
  ('wis', 100, 5, 10, 5);

INSERT INTO stat_skill_scale VALUES
  ('fire', 'str', 10),
  ('water', 'str', 6),
  ('lightning', 'str', 6),
  ('wind', 'str', 2),
  ('earth', 'str', 4),
  ('sonic', 'str', 8),
  ('martial', 'str', 10),
  ('material', 'str', 2),
  ('shift', 'str', 8),
  ('life', 'str', 2),
  ('space', 'str', 4),
  ('gravity', 'str', 10),
  ('fire', 'dex', 6),
  ('water', 'dex', 2),
  ('lightning', 'dex', 8),
  ('wind', 'dex', 10),
  ('earth', 'dex', 2),
  ('sonic', 'dex', 10),
  ('martial', 'dex', 6),
  ('material', 'dex', 10),
  ('shift', 'dex', 2),
  ('life', 'dex', 4),
  ('space', 'dex', 8),
  ('gravity', 'dex', 4),
  ('fire', 'int', 8),
  ('water', 'int', 4),
  ('lightning', 'int', 10),
  ('wind', 'int', 4),
  ('earth', 'int', 8),
  ('sonic', 'int', 6),
  ('martial', 'int', 2),
  ('material', 'int', 6),
  ('shift', 'int', 4),
  ('life', 'int', 8),
  ('space', 'int', 10),
  ('gravity', 'int', 2),
  ('fire', 'wis', 4),
  ('water', 'wis', 10),
  ('lightning', 'wis', 2),
  ('wind', 'wis', 6),
  ('earth', 'wis', 6),
  ('sonic', 'wis', 2),
  ('martial', 'wis', 4),
  ('material', 'wis', 8),
  ('shift', 'wis', 6),
  ('life', 'wis', 10),
  ('space', 'wis', 6),
  ('gravity', 'wis', 8)
  ('fire', 'vit', 2),
  ('water', 'vit', 8),
  ('lightning', 'vit', 4),
  ('wind', 'vit', 8),
  ('earth', 'vit', 10),
  ('sonic', 'vit', 4),
  ('martial', 'vit', 8),
  ('material', 'vit', 4),
  ('shift', 'vit', 10),
  ('life', 'vit', 6),
  ('space', 'vit', 2),
  ('gravity', 'vit', 6);

INSERT INTO vital_scale VALUES
  (0, 10, 0, 2, 1);

INSERT INTO monster (id, name, hp, xp, level) VALUES
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'Puddling', 333, 111, 1),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'Baurus', 680, 280, 3),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'Highwayman', 1320, 640, 5),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'Wandering Berzerker', 2001, 900, 7),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'Cursed Cube', 4096, 2197, 9),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'Basalt-Skin Bear', 5800, 3190, 11),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'Displacer Treant', 7777, 4444, 13),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'The Innkeeper''s Door', 10000, 10000, 15);

INSERT INTO monster_skill (monster_id, skill, ranks, spec1_name, spec1_ranks, spec2_name, spec2_ranks) VALUES
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'fire', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'water', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'lightning', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'wind', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'earth', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'sonic', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'martial', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'material', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'shift', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'life', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'space', 0, '', 0, '', 0),
 ('269f9976-b499-4f99-8b07-f8c64c34aff3', 'gravity', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'fire', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'water', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'lightning', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'wind', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'earth', 4, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'sonic', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'martial', 4, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'material', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'shift', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'life', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'space', 0, '', 0, '', 0),
 ('5c6da319-8a84-47ab-9c67-7bd3ea2a0e47', 'gravity', 4, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'fire', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'water', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'lightning', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'wind', 7, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'earth', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'sonic', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'martial', 9, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'material', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'shift', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'life', 0, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'space', 9, '', 0, '', 0),
 ('78dd427c-88df-40c4-a3f8-38f4e1cd527a', 'gravity', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'fire', 10, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'water', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'lightning', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'wind', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'earth', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'sonic', 9, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'martial', 10, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'material', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'shift', 10, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'life', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'space', 0, '', 0, '', 0),
 ('b1fd091f-5fe9-4efd-a8c9-04b6fbac2bb2', 'gravity', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'fire', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'water', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'lightning', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'wind', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'earth', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'sonic', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'martial', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'material', 5, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'shift', 0, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'life', 13, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'space', 13, '', 0, '', 0),
 ('7a388f50-8610-4acd-a529-24154e6ae82a', 'gravity', 13, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'fire', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'water', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'lightning', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'wind', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'earth', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'sonic', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'martial', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'material', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'shift', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'life', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'space', 0, '', 0, '', 0),
 ('421f2a94-b09e-4698-adbc-c192ea41b625', 'gravity', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'fire', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'water', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'lightning', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'wind', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'earth', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'sonic', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'martial', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'material', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'shift', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'life', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'space', 0, '', 0, '', 0),
 ('038437a7-d4ff-4dbd-9021-17f20b86712c', 'gravity', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'fire', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'water', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'lightning', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'wind', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'earth', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'sonic', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'martial', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'material', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'shift', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'life', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'space', 0, '', 0, '', 0),
 ('f59eec6c-2fd1-4c32-bf9f-7ce548d2ad0b', 'gravity', 0, '', 0, '', 0);

INSERT INTO character (id, user_id, name, level, xp, hp_current, mp_current) VALUES
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'roland', 'Roland', 50, -1, -1, -1),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'hyperion', 'Hyperion', 50, -1, -1, -1),
 ('710a70ee-f436-4cec-b323-419e515cb046', '~template~', 'Template', 50, -1, -1, -1);

INSERT INTO character_skill VALUES
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'fire', 15, 'Burn', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'water', 15, 'Freeze', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'lightning', 15, 'Shock', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'wind', 15, 'Stagger', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'earth', 15, 'Root', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'sonic', 15, 'Deafen', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'martial', 15, 'Martial Training', 5, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'material', 20, 'Bestow', 10, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'shift', 3, '', 0, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'life', 39, 'Animate', 25, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'space', 20, 'Summon', 10, '', 0),
 ('150b0c3d-db29-4a2f-b8b4-edec40ba6dff', 'gravity', 20, 'Flight', 10, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'fire', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'water', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'lightning', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'wind', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'earth', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'sonic', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'martial', 97, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'material', 9, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'shift', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'life', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'space', 0, '', 0, '', 0),
 ('6e490be9-35ae-4fb2-9fba-ead60fd75a23', 'gravity', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'fire', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'water', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'lightning', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'wind', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'earth', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'sonic', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'martial', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'material', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'shift', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'life', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'space', 0, '', 0, '', 0),
 ('710a70ee-f436-4cec-b323-419e515cb046', 'gravity', 0, '', 0, '', 0);
