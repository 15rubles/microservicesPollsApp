insert into `user`( auth, `password`, username)
values ( 0,'$2a$10$Rn0.DLCNVpEuKiZf00C.VOvDZ4Sxk4Pe2qepKSTMJILNLbTkymO9m', 'user'),
       ( 2,'$2a$10$Rn0.DLCNVpEuKiZf00C.VOvDZ4Sxk4Pe2qepKSTMJILNLbTkymO9m', 'lead'),
       ( 1,'$2a$10$Rn0.DLCNVpEuKiZf00C.VOvDZ4Sxk4Pe2qepKSTMJILNLbTkymO9m', 'admin');

insert into user_allowed_polls (user_id, allowed_polls)
values (1,1),
       (1,3);