import me.func.protocol.DropRare
import me.func.protocol.personalization.Graffiti
import me.func.protocol.personalization.GraffitiPack
import java.util.UUID

fun createPack(
    title: String,
    author: String,
    price: Int,
    rare: DropRare,
    counter: Int,
    vararg graffitiUuid: String
) = graffitiPacksUuid[counter] to GraffitiPack(graffitiPacksUuid[counter], graffitiUuid.mapIndexed { index, uuid ->
    Graffiti(uuid, 83 + 192 * (index % graffitiUuid.size), counter * 192, 192, "func")
}.toMutableList(), title, author, price, rare.ordinal, true)

val graffitiPacksUuid = listOf(
    "307264a1-2c69-11e8-b5ea-1cb72caa35fd",
    "307264a1-2c69-11e8-b5ea-1cb72caa35fa",
    "a7f4b7d3-c44f-4bfa-89e5-57c84a6410cd",
    "c46002de-25ef-404a-b3ea-df66d142bb81",
    "6a0b6461-13da-4d8d-afc2-8b391097605f",
    "43c2c419-e866-4a2b-8778-6e4b35c95d6a",
    "92c5d468-829d-4ea5-bd39-2669681624dc",
    "4d48c377-540b-4313-b870-bc03c9b47993",
    "deaafb8d-5254-40da-942e-9f7a91d9ced3",
    "7f1acdca-03de-40b4-ba47-f242e6ac36d3",
).map { UUID.fromString(it) }

val actualGraffitiPacks = mutableMapOf(
    createPack(
        "Ты котик!", "func", 99, DropRare.COMMON, 0,
        "307264a1-2c69-11e8-b5ea-1cb72caa35f1",
        "307264a1-2c69-11e8-b5ea-1cb72caa35f2",
        "307264a1-2c69-11e8-b5ea-1cb72caa35f3",
        "299fc13f-280a-48de-9ae4-99bbe92116a1",
        "4f25a82e-6197-43f5-bd20-2200bbf96fa7",
        "c530a244-422c-41df-8854-dfa1f23bd890",
        "b99261af-c4e9-4a5e-bdde-3438c36b15f4",
    ),
    createPack(
        "Домашняя Лиса", "func", 99, DropRare.COMMON, 1,
        "d647e5ab-676c-4f08-9e07-46ae8d368f1b",
        "803d713e-787c-43d1-af90-b2e6207e57aa",
        "714f520c-fddb-44db-ba76-a83fafb5b152",
        "71428b20-7249-429c-9b27-b2854803d314",
        "302e569f-6e84-4a66-8536-2000fc288dce",
        "728628d3-ad54-44ad-a3cb-40a49303e284",
        "f1411866-4906-4217-9887-bdaffa8af56f",
    ),
    createPack(
        "Тест 3", "func", 999, DropRare.COMMON, 2,
        "69ff2678-d869-4858-9211-7501f97dd69b",
        "641a2440-d5f5-4ab4-80e8-10b8f076499a",
        "c9ce4f1b-f4b7-4031-be2d-0c40bc964e16",
        "4cdc8c23-4db5-49fe-81fd-6486f66c7aff",
        "8ac71aa5-f34b-4e44-8591-0bebb5596676",
        "58487e34-0faf-4d32-ad4f-78e7ca9783e0",
        "6fee1070-a907-4aa4-918e-006f2cbce616",
    ),
    createPack(
        "Тест 4", "func", 999, DropRare.COMMON, 3,
        "0f0e9808-4faf-4ba3-8793-f5f87f40044f",
        "2fda2abd-261c-440b-9384-09794f510ec0",
        "2a00d4ea-06ba-4966-b914-f63badf0dbd7",
        "4bab1c4d-95c8-4e2d-904c-3ddcc62de4de",
        "a85430f7-5eeb-477e-a138-9de09c07366e",
        "57641929-4269-4ac7-bbd0-72ce6d8c8013",
        "fe75c6f8-4977-4c18-a421-5bab0b799652",
    ),
    createPack(
        "Тест 5", "func", 999, DropRare.COMMON, 4,
        "16a95c62-f9d7-4b3b-bc0b-e75415abc5d4",
        "5851f3b2-e5cf-4c3f-8642-152f2717d1b5",
        "5ca5eb2d-8074-4d3f-b7f2-11e29ade77a6",
        "58863006-0c97-4318-8617-50b3d95cdf9f",
        "edfb758a-377b-4105-a7d4-8bf4deed3a0a",
        "012dbdc8-7a8d-4a1a-8612-c48063b2726e",
        "0629ec28-e683-4633-9a02-c68ab19b2a20",
    ),
    createPack(
        "Тест 6", "func", 999, DropRare.COMMON, 5,
        "b75db498-2b78-4639-9cc1-667f935e86b4",
        "6afd3c08-85f6-45a0-bea7-530601318e04",
        "dbd7a57d-b8e0-48c0-97b3-4f46fabd3250",
        "bac0a1e0-7d3f-413c-af90-9e4bdf2c1fc0",
        "03e3b6ae-5c4b-421a-89b7-921888532b96",
        "3a2f8713-d234-4273-bea3-b945ecb9516e",
        "1ec24bf8-4844-4b58-811d-76006c73b688",
    ),
    createPack(
        "Тест 7", "func", 999, DropRare.COMMON, 6,
        "05bd7557-7c18-4974-a01d-595c2b3a1578",
        "63b2b097-9a45-459c-9f1d-337a3ae5a099",
        "6d9087c1-9d0b-4199-a890-61e9269547fa",
        "efa9472d-1660-4400-8df1-e01af31ab1f4",
        "4bcb9ec9-e42b-44ac-91bf-a4a415b96abf",
        "dcdda835-1931-4c5d-a433-6327eba7b6c2",
        "5c190495-74e8-4ac0-a0d3-f98d510fc07b",
    ),
    createPack(
        "Тест 8", "func", 999, DropRare.COMMON, 7,
        "862fb214-82d9-4b1e-825a-fe3ca0fbf488",
        "0420ed16-d2c5-43a4-85d8-f9efa9386418",
        "1e62d38c-73de-4d64-a974-29bcd5047890",
        "c4ffaaf1-fb4c-4c82-a49b-98a813a99c7f",
        "54615d94-98e2-4eda-bc6a-386ca35086c7",
        "13f0d871-0798-496d-83fd-9c853beff015",
        "04a18e4b-1cf2-4e59-8608-4d87d819088e",
    ),
    createPack(
        "Тест 9", "func", 999, DropRare.COMMON, 8,
        "bb2978d3-fd70-4672-a621-b6e1849bcdc1",
        "bf827e4f-301b-4932-b37d-bc3c4516baa3",
        "584d315f-e347-41f1-892d-97a72cee654d",
        "92614f67-814b-45ff-8cac-2d72293cbe84",
        "076bd846-52e8-4544-8ad0-4568464c8ee4",
        "9b71de91-0dbd-4751-96ba-21ec33d9cd80",
        "6d95cece-1dd2-48e0-b6e2-ad50a148f006",
    ),
    createPack(
        "Тест 10", "func", 999, DropRare.COMMON, 9,
        "9e610970-1b38-4fd8-9881-b5f36d09da22",
        "f925a361-7d74-4a2d-9d6a-dbbfe980601e",
        "e362b228-e5a6-4af4-9904-566e2c3cf013",
        "de4a8b35-3ceb-4800-8144-e578cdd1463a",
        "d9dbd4a1-2238-4081-9756-7bab77d9f189",
        "7924f8de-b9d2-499e-9014-a9b4bd3a0861",
        "8db35be6-69f0-4946-a506-ce40b571e6fc",
    ),
)