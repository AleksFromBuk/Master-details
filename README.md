# Master-Detail Application

Данное приложение демонстрирует типовую связку "Document" (мастер) и "DocumentDetail" (деталь) с учетом ТЗ.  
### Стек:
*Spring Boot*,
 *Hibernate*,
 *PostgreSQL*, *Liquebase*, *Thymeleaf*.


## Запуск
1. Если установлен Docker: 
запустите на выполнение *services* в файле docker-compose.yml, который в корне проекта
   ```bash 
   a) 'services:'
   б) или командой в терминале 'docker-compose up'  
    

2. Если на локальной машине установлена БД PostgreSQL, можно часть application.yml, которая 
закомментрована - раскоментировать, а текущую-активную наоброт - закомментировать.

При этом учитывать: 

схема: *default*

url: *jdbc:postgresql://localhost:5432/postgres* 

username: *postgres*

password: *password*

## Соберите и запустите приложение:
   1. mvn clean package
   java -jar target/masterdetail-1.0.0.jar

   2. либо использовать интегрированную среду разработки, например, Intellij Idea

## Использование

Точка входа: 

*Master-Detail\src\main\java\org\example\masterdetail\MasterDetailApplication.java*

Приложение поднимается на http://localhost:8080/

### Документы
Список документов(*можно запустить тестовый класс DocumentControllerIntegrationTest на выполнение - 
в результате появится что-то в таблице докуентов и отображение списка не будет пустым*): http://localhost:8080/documents

Отображает все документы, а также их спецификации.

Для создания нового документа нажать 
##### Add Document
и заполнить значения свойств экзмепляра документа и далее *save*.

В форме отображаемого списка документов есть кнопки *Edit* и *Delete*,
для выполнения соответствующего действия над выбранным экземпляром документа.

### Спецификации
Добавить спецификацию:  
##### http://localhost:8080/documents/{docId}/details/new
Результат — редирект на http://localhost:8080/documents

Для редактирования или удаления спецификации: http://localhost:8080/documents/{docId}/details/{deatailId}}/edit

--- звершаем действие нажатием соответствующих кнопок формы

### Логирование 

В частности, ошибки валидации значений свойств, например, отсутсвие последних при попытки сохранить - 
отображается пользователю и логируется в таблицу *error_log* БД отдельной транзакицией.

