set serveroutput on;
create or replace trigger trigger_name
before insert or update or delete on cursorTable
for each row
when (new.id > 0) 

declare

begin
    dbms_output.put_line('ID ' || :old.id);
end;
/


begin
    insert into cursorTable values(3, 'prashant', 600);
end;