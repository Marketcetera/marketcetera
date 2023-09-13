insert into metc_permissions values(((select max(id) from metc_permissions)+1),now(),0,'Access to read ltrade suggestions','ViewSuggestionsAction');
insert into metc_permissions values(((select max(id) from metc_permissions)+1),now(),0,'Access to create ltrade suggestions','SendSuggestionAction');
insert into metc_roles_permissions select a.id as roles_id,b.id as permissions_id from metc_roles a,metc_permissions b where b.name like '%Suggestion%Action' and (a.name='Trader' or a.name='TraderAdmin');
