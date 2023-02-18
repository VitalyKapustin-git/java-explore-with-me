# Афиша событий
Данное приложение позволяет создавать события и делиться ими с другими людьми.

Реализована доп функциональность, с помощью которой люди смогут комментировать события.
Каждый человек сможет оставлять столько комментариев, сколько потребуется. Так же есть возможность редактировать свои комментарии.
Для комментариев доступны следующие эндпоинты которые условно поделены на области доступности по аналогии с основным приложением:

###### Public
* GET("/event/{eventId}/comments") - Список всех комментариев для события
* GET("/comments/{commentId}") - Получение комментария по id


###### Private
* GET("/user/{userId}/event/{eventId}/comments") - Комментарии пользователя для конкретного событии
* GET("/user/{userId}/comments") - Все комментарии пользователя
* POST("/user/{userId}/event/{eventId}/comments") - Создание комментария пользователем
* PATCH("/user/{userId}/event/{eventId}/comments") - Правка собственного комментария
* DELETE("/user/{userId}/comments/{commentId}") - Удаление собственного комментария для события


###### Admin
* DELETE("/admin/comments/{commentId}") - Удаление любого комментария
* PATCH("/admin/comments") - Редактирование любого комментария

Все эндпоинты тестируются тестами postman.
Связь комментария со смежными сущностями (события, пользователи) односторонняя. 
На момент реализации фичи нет потребности отображать комментарии в смежных сущностях.
