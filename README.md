# Cristalix Animation API DOCS (актуальная версия 2.8.5)

![image](https://user-images.githubusercontent.com/42806772/149049028-a99c790a-224a-48c5-b3a2-58989900fd3e.png)
<br>

<br>
<h2>Зачем нужно использовать этот инструмент?</h2>

1. Чтобы добавлять новые глобальные моды взаимодействующих с сервером (например новая персонализация - граффити)
2. Для упращения работы с модами, вынесенные утилиты вам помогут не писать лишний код
3. Автоматическое обновление базового пакета модов (стандартный пакет, квесты, лутбокс и прочее)
4. Единый стиль и оформление режимов
   <br>

<h2>Как подключить? Какие модули есть?</h2>

<h3>Get Started with `gradle`</h3>

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://repo.c7x.dev/repository/maven-public/'
        credentials {
            username System.getenv("CRI_REPO_LOGIN")
            password System.getenv("CRI_REPO_PASSWORD")
        }
    }
}

dependencies {
    implementation 'me.func:animation-api:2.8.5'
}
```

<h3>Модули</h3>

`STANDARD` - стандартный набор модов (by <a href="https://vk.com/kostyan_konovalov">@fiwka</a>
, <a href="https://vk.com/funcid">@func</a>, <a href="https://vk.com/delfikpro">@delfikpro</a>
, <a href="https://vk.com/sworroo">@sworroo</a>)<br>
`DIALOG` - кит с квестовыми диалогами (by <a href="https://vk.com/sworroo">@sworroo</a>
, <a href="https://vk.com/funcid">@func</a>)<br>
`LOOTBOX` - набор для подключения лутбоксов (by <a href="https://vk.com/delfikpro">@delfikpro</a>
, <a href="https://vk.com/funcid">@func</a>)<br>
`MULTICHAT` - набор для мультичата (by <a href="https://vk.com/zabelovq">@zabelov</a>
, <a href="https://vk.com/izenkk">@zenk</a>)<br>
`GRAFFITI` - кит с клиентом к сервису граффити и подключением мода (by <a href="https://vk.com/funcid">@func</a>)<br>
`EXPERIMENTAL` - экспериментальный набор для тестирования новых модов (by <a href="https://vk.com/funcid">@func</a>)<br>
`NPC` - модуль для работы с NPC (by <a href="https://vk.com/funcid">@func</a>)<br>
`BATTLEPASS` - модуль для работы с BattlePass (by <a href="https://vk.com/akamex">@akamex</a>
, <a href="https://vk.com/funcid">@func</a>)<br>
`HEALTH_BAR` - модуль добавляющий полоску здоровья (by <a href="https://vk.com/delfikpro">@delfikpro</a>)<br>
`DEBUG` - модуль для удобной разработки модов (by <a href="https://vk.com/func">@func</a>
, <a href="https://vk.com/kostyan_konovalov">@fiwka</a>)<br>

<h3>Подключение модулей</h3>

При старте плагина напишите `Anime.include(Kit.STANDARD)`, так вы подключите стандартный набор модов, если вам нужны
другие модули, например Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD (другие будут добавлены позже), то укажите их через
запятую: `Anime.include(Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD)`
<br>
Моды берутся по вшитой ссылке на `cristalix storage`, если вы хотите заменить ссылку на наборы модов,
используйте `env MOD_STORAGE_URL`, ссылка должна оканчиваться на `/`, не рекомендуется.
<br>
Если вам не нужны граффити или нужно ограничить доступные места -
используйте `Anime.modifyGraffitiPlaceCondition(canPlace: Predicate<Location>)`

<h2>Скачивание, загрузка и отправка ваших модов - утилита ModLoader</h2>

<h3>Быстрый старт без подробностей<h3>

![image](https://user-images.githubusercontent.com/42806772/166143350-fc66de27-e38b-4866-b764-2a73032c62bf.png)

```kotlin
  // Вариант 1
ModLoader.loadFromWeb("https://storage.c7x.ru/func/cristalix-dialogs.jar") // Загрузка мода в стандартную папку MOD_LOCAL_DIR_NAME
ModLoader.onJoining("cristalix-dialogs") // Загрузка мода в плагин, отправка игрокам мода при входе

// Вариант 2
ModLoader.onJoining(
    "mod-bundle",
    "cristalix-dialogs"
) // Пойдет искать загруженные моды, если кто-то не загружен, загрузит его из стандартной папки "anime", затем моды будут отправлены игрокам при входах на сервер

// Вариант 3
ModLoader.load("mods/mod-bundle.jar") // Загрузит мод по пути
ModLoader.send("mod-bundle.jar", player) // Отправит мод игроку

// Вариант 4
ModLoader.loadAll("mods") // Загрузит все моды из папки mods
ModLoader.send("mod-bundle.jar", player) // Отправит мод игроку
```

<h3>Скачивание модов (интернет -> машина)</h3>

`download(fileUrl: String, saveDir: String)` скачивание файла по ссылке, в указанную папку (если файл уже скачан - он
перезапишется), пример `download("http://cristalix.ru/cristalix-standard-mod.jar", "mods")`

<h3>Загрузка модов в плагин (машина -> оперативная память)</h3>

`loadFromWeb(fileUrl: String, saveDir: String = MOD_LOCAL_DIR_NAME)` загрузка файла в стандартную папку `anime` по
адресу, затем прогрузка мода на уровнь плагина<br>
`loadManyFromWeb(vararg fileUrls: String)` то же самое, но можно указывать адреса через запятую<br>

`load(filePath: String)` прогрузка мода указав путь к файлу (не web), мод сохраняется по ключу имени файла,
например `mod-bundle.jar`<br>
`loadAll(dirPath: String)` загрузить все моды из папки по путю к папке<br>

<h3>Отправка мода игроку (оперативная память -> клиент игрока)</h3>

`send(modName: String, player: Player)` отправка мода игроку, пример имени `mod-bundle.jar`<br>
`manyToOne(player: Player)` отправить все прогруженные моды игроку<br>
`oneToMany(modName: String)` отправить один мод по имени всем игрокам<br>
`sendAll()` отправить все прогруженные моды всем игрокам<br>

<h2>Отправка данных на моды - конструктор ModTransfer</h2>

![image](https://user-images.githubusercontent.com/42806772/166143593-594bba87-0879-4843-b439-b0ec286c94c6.png)

```kotlin
// Пример 1 (Умный конструктор)
ModTransfer("Осталось %n% жизней!", 76666).send("func:health", player)

// Пример 2 (Билдер)
ModTransfer()
    .item(ItemStack(AIR)) // Предмет, можно NMS, Bukkit
    .nbt(NBTTagCompound()) // Любой тег
    .string("Привет") // Строка
    .json(object) // Любой объект, будет превращен в JSON строку
    .byteBuffer("heyy".bytes()) // Массив byte
    .double(43.5) // Прочие числа
    .varInt(4)
    .putDouble(5.6) // Методы c "put" для Java
    .send("func:lol", player)

// Привет 3 (Переиспользование буффера) 
val data = ModTransfer().integer(items.size)
    .item(item.itemStack)
    .string(item.title)
    .string(item.rare.name)

players.forEach { player -> data.send("lootbox", player) }  
```

<h3>Режим DEBUG (Для удобной разработки)</h3>

Простым языком: вы кидаете мод в папку, API это видит и перезагружает мод у игроков.

Данный режим позволяет мнгновенно обновлять мод на клиенте игрока, что очень удобно для быстрого тестировая. Обычные
этапы разработки: написание кода -> компиляция -> заливка мода в папку сервера -> перезагрузка сервера -> вход на сервер
-> загрузка мода, всего 6 этопов. С данным режимом: написание кода -> компиляция -> заливка мода в папку сервера ->
смотрим, всего 4 этапа. Так же можно автоматически загруждать мод в папку прямо из среды разработки, но это уже задача
вашей среды.
Чтобы включить режим быстрого тестирования, допишите в `Anime#include` кит `Kit.DEBUG`. По умолчанию папка `mods`
является хранилищем тестовых модов, чтобы сменить стандартную папку - измените переменную среды `MOD_TEST_PATH`.

<h3>Прогресс Bars EXPERIMENTAL</h3>

<img src="https://user-images.githubusercontent.com/42806772/189779202-e2fd1ab2-3014-4753-b75e-06f5c018f7b3.png" width="500">

На данный момент реализован только реактивный прогрессбар (меняете поля на сервере - меняются на клиентах), работает на экране игрока.

Создание на языка java
```java
ReactiveProgress.builder() // примеры того, что можно настроить
   .position(Position.BOTTOM) // указать стартовую позицию снизу
   .offsetY(32) // сместить вверх на 32 "пикселя"
   .hideOnTab(false) // не скрывать при нажатии таба
   .color(GlowColor.GREEN) // поставить зеленый цвет, можно поставить любой RGB, например через Tricolor
   .build();
```

Чтобы показать его группе игроков и подписать их на обновление, используйте `ReactiveProgress#send(vararg players: Player)`
Отписать игроков от обновления - `ReactiveProgress#unsubscribe(vararg players: Player)`
Стереть прогресс у игроков и отписать от обновления - `ReactiveProgress#delete(vararg players: Player)`

Как реактивно менять данные прогрессбара?
- Уровень прогресса, вызовите `ReactiveProgress#setProgress(value: Double)` (число от 0 до 1)
- Строку над прогрессом, вызовите `ReactiveProgress#setTest(test: String)`

<h3>Меню выбора / Меню выбора подрежима STANDARD</h3>

Selection
   
<img src="https://user-images.githubusercontent.com/42806772/167634413-839af5f3-bf3a-469b-ae33-8a50a4785b6f.png" width="500">
   
Choicer
   
<img src="https://user-images.githubusercontent.com/42806772/169326395-2e1b4119-5a6e-45db-b1e2-c377c5410d18.png" width="500">

1. Чтобы загрузить свою текстуру, используйте `Anime#loadTextures` или `Anime#loadTexture` (возвращает texture).
2. Если вы хотетите сменить валюту, то нужно изменить поле `vault` в `Selection` на символ валюты или путь к текстуре
3. Если нужно закрыть меню, используйте `Anime#close`, которое закрывает любое окно игрока, кроме меню достижений и настроек.

Пример для языка kotlin
```kotlin
val menu = selection {
    title = "Прокачки" // Название слева сверху
    money = "Ваш баланс 999" // Баланс справа сверху, если не указать - монетка не покажется
    hint = "Взять" // При наведении на предмет, будет отображаться этот текст
    rows = 3 // Количество строчек
    columns = 4 // Количество колонок
    buttons( // Указание кнопок, если кнопок будет больше чем вмещается, меню станет многостраничным
        button {
            texture = "minecraft:textures/items/apple.png" // Текстура namespace:path
            price = 88L // Цена предмета, если не указать - монета не покажется
            title = "Куриные\nкрылья" // Название предмета (есть поддержка \n)
            description = "+10 силы" // Комментарий предмета (есть поддержка \n)
            onClick { player, index, button -> // Обработка нажатия по кнопке
                player.sendMessage("Button id: $index, button $button")
            }
        },
        button {
            texture = Sprites.DUO.path() // Текстура с двойным режимом
            price = 88L // Цена предмета, если не указать - монета не покажется
            title = "Куриные\nкрылья" // Название предмета (есть поддержка \n)
        },
        button {
            item = ItemStack(Material.HEAD) // Добавление иконки кнопки по предмету
            price = 99
            hint = "Продать" // Надпись при наведении на кнопку
            title = "Картошка"
            description = "+гниль"
            hover = "Привет" // Всплывающий текст при наведении
        }.sale(60) // Указать скидку 60%
    )
}

menu.open(player) // Открываем меню игроку, можно сохранять меню в переменную и показывать много раз

// Кнопки можно менять реактивно после открытия меню, ставите title и она меняется у игроков 
```
Пример для языка java
```java
Selection menu = new Selection(
        "Прокачки",
        "Баланс 999",
        "Купить",
        4, // Количество строчек
        3, // Количество колонок
        new ReactiveButton()
            .texture("minecraft:textures/items/apple.png")
            .price(999)
            .title("Название")
            .description("описание")
            .hover("Текст при наведении")
            .onClick((player, index, button) -> {
               player.sendMessage("Button id: $index, button $button");
            }), 
        new ReactiveButton().material(Material.HEAD).sale(30) // Иконка по предмету/материалу, скидка 30%
);

menu.open(player);
```
   
<h3>Меню реконнекта STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/168923320-5465ff25-70be-4324-9371-c830ba31a43c.png" width="500">
 
Пример для языка kotlin
```kotlin
var menu = Reconnect { player -> player.sendMessage("Возвращаем вас в игру!") } // Первый вариант конструктора
menu = Reconnect(90) { player -> player.sendMessage("Возвращаем вас в игру!") } // Теперь указываем сколько секунд осталось
menu = Reconnect("Продать почку", 90, "Продать") { player -> player.sendMessage("Возвращаем вас в игру!") } // Теперь указываем все что только можно
   
menu.open(player) // Открываем меню игроку
```
   
<h3>Меню подтверждения STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/168159225-348a4e78-d9c1-44c1-818f-00e5d15fe395.png" width="500">

Позволяет удостовериться, в корректности мотивов игрока. Поддерживает несколько строк.
   
Пример для языка kotlin
```kotlin
val menu = Confirmation("Купить Уганду за", "602 рубля", "Или все же нет?") { player -> // Через запятую указываем строки сообщения меню
   player.sendMessage("Успешная покупка!") // Пишем что делать при подтверждении игроком
}
menu.open(player) // Открываем меню игроку
```
Пример для языка java
```java
Confirmation menu = new Confirmation(Arrays.asList("Купить Уганду за", "602 рубля", "Или все же нет?"), player -> { // Через запятую указываем строки сообщения меню
    player.sendMessage("Успешная покупка!") // Пишем что делать при подтверждении игроком
});

menu.open(player); // Открываем меню игроку
```
   
<h3>Батлпасс BATTLEPASS</h3>

<img src="https://i.imgur.com/3R8pYtH.png" width="500">

Что представляет из себя батлпасс?<br>
Это приобретаемый набор заданий, за выполнение которых даются внутриигровые вещи.<br>

```kotlin
val battlePass = BattlePass.new(399) {
    pages = arrayListOf(
        BattlePassPageAdvanced(
            // Первая страница баттлпасса
            300, // Опыт для прохождения уровня
            10, // Цена скипа уровня
            listOf(
                // Список обычных предметов
                NameTag.TAG1.getIcon(),
                ArrowParticle.SLIME.getIcon(),
                ArrowParticle.WATER_DROP.getIcon(),
                StepParticle.SLIME.getIcon(),
                Mask.HOUSTON.getIcon(),
                KillMessage.GLOBAL.getIcon(),
                MoneyKit.SMALL.getIcon(),
                ArrowParticle.FALLING_DUST.getIcon(),
                ArrowParticle.SPELL_INSTANT.getIcon(),
                Mask.JASON.getIcon(),
            ),
            listOf(
                // Список премиум предметов
                NameTag.TAG3.getIcon(),
                Mask.TRADEGY.getIcon(),
                Mask.HORROR.getIcon(),
                MoneyKit.SMALL.getIcon(),
                NameTag.TAG28.getIcon(),
                Mask.COMEDY_MASK.getIcon(),
                KillMessage.END.getIcon(),
                ArrowParticle.REDSTONE.getIcon(),
                StepParticle.REDSTONE.getIcon(),
                Corpse.G1.getIcon(),
            ),
        ),
        BattlePassPageAdvanced(
            // Вторая страница баттлпасса
            600,
            20,
            listOf(
                KillMessage.DEAD.getIcon(),
                MoneyKit.SMALL.getIcon(),
                Mask.SCREAM.getIcon(),
                NameTag.TAG10.getIcon(),
                StepParticle.REDSTONE.getIcon(),
                KillMessage.ROOM.getIcon(),
                Mask.DALLAS.getIcon(),
                NameTag.TAG16.getIcon(),
                Mask.CREWMATE_LIME.getIcon(),
                MoneyKit.NORMAL.getIcon()
            ),
            listOf(
                MoneyKit.SMALL.getIcon(),
                KillMessage.SLEEP.getIcon(),
                Mask.CREWMATE_WHITE.getIcon(),
                NameTag.TAG17.getIcon(),
                Mask.JASON.getIcon(),
                ArrowParticle.VILLAGER_ANGRY.getIcon(),
                NameTag.TAG20.getIcon(),
                MoneyKit.NORMAL.getIcon(),
                Mask.CREWMATE_PURPLE.getIcon(),
                StepParticle.VILLAGER_ANGRY.getIcon()
            ),
        )
    )
    sale(50.0) // Скидка 50%
    facade.tags.add("Выполняйте квесты - получайте призы!")
    facade.tags.add("BattlePass завершится в 01.04.2022")
}

BattlePass.send(player, battlePass)
BattlePass.show(player, battlePass, BattlePassUserData(100, false))
```

Редкость предмета берется из `NBTTag` предмета `rare` (DropRare), а статус собранности награды `taken` (Boolean).<br>
Можно указывать награду на каждый уровень, требуемый опыт.<br>

<h3>Баннеры STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/148836905-914c3f10-bf65-4767-b28d-7a884fac6acd.png" width="250">

Что представляет из себя баннер?<br>
Это голограмма, рисунок в мире, маркер и прочее что является прямоугольником в мире.<br>

```kotlin
Banners.new {
    motionType = MotionType.CONSTANT // тип движения
    watchingOnPlayer = false // смотрит ли баннер вечно на игрока
    motionSettings = hashMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0
    ) // магия
    content = "" // текст на прямоугольнике
    x = 0.0 // координата x
    y = 0.0 // координата y
    z = 0.0 // координата z
    height = 100 // высота
    weight = 100 // ширина
    texture =
        "" // текстура, например minecraft:apple (не забывайте что текстуру можно загружать через мод по web-ссылке)
    red = 0 // красный цвет прямоугольника
    green = 0 // зеленый цвет прямоугольника
    blue = 0 // синий цвет прямоугольника
    opacity = 0.62 // степерь прозрачности, от 0.0 до 1.0
}
```

Что за типы движения?<br>
`STEP_BY_TARGET` - баннер следует за сущностью
`PERIODIC` - баннер двигается по периодичной траектории
`CONSTANT` - баннер не двигается

<img src="https://user-images.githubusercontent.com/42806772/148837007-d52b93ca-1a52-4507-9c9b-648a05d3ec6d.png" width="250">

Как создать Banner?<br>

```kotlin
// Kotlin пример
Banners.new { // Баннер добавится в список баннеров и когда игроки будут заходить, они его увидят
    // Элементарная заменя характеристик (указана лишь часть)
    x = 1.0
    y = 100.0
    z = 1111000.0
    texture = "minecraft:apple"
    red = 255
    opacity = 1.0
    content = "Этот текст -\nмногострочный"
    height = 120
    weight = 200

    // Продвинутые методы для замены свойств
    location(player.location) // Указание места баннера через локацию
    eyeLocation(player.bedSpawnLocation) // Указание поворота через точку, на которую нужно смотреть
    target(player, 0.0, 0.0, 2.1) // Теперь баннер следует за игроком, баннер выше игрока на 2.1 блока
    shineBlocks(false) // Чтобы баннер не просвечивался через стены
}
```

```java
// Java пример
Banner banner = Banner.S.builder() // Создание баннера
    .x(location.x)
    .y(location.y+50)
    .z(location.z)
    .opacity(0.0) // Прозрачность, от 0.0 до 1.0
    .height(50) // Высота
    .weight(50) // Ширина
    .content("§aЯ голограмма","§eОчень красивая","§cИ приятная глазу") // Многострочный текст
    .watchingOnPlayer(true) // Указание, чтобы баннер всегда смотрел на игрока
    .build();

    Banners.add(banner); // Добавление в список баннеров, которые будут показаны игроку при входе на сервер
```

Методы утилиты `Banners`:<br>
`Banners.new(data: Banner.() -> Unit): Banner` kotlin версия конструктора для баннера (пример выше)<br>
`Banners.new(banner: Banner): Banner` java версия конструктора для баннера (пример выше)<br>
`Banners.content(player: Player, uuid: UUID, content: String)` отправка нового текста игроку на баннер<br>
`Banners.content(player: Player, banner: Banner, content: String)` тоже самое, но через объект баннера<br>
`Banners.show(player: Player, vararg uuid: UUID)` показать игроку все указанные баннеры по uuid (баннеры уже есть в
списке глобальных)<br>
`Banners.show(player: Player, vararg banner: Banner)` показать игроку все баннеры (они могут не быть в списке баннеров и
созданы через конструктор `Banner`)<br>
`Banners.remove(uuid: UUID)` удалить баннер из списка (не удаляет баннер игрокам)<br>
`Banners.hide(player: Player, vararg uuid: UUID)` удалить игроку все указанные баннер(ы)<br>
`Banners.hide(player: Player, vararg banner: Banner)` тоже самое, но через объекты класса Banner<br>

<h3>Индикаторы STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913370-bb9ecbad-a5be-40c0-95b2-158b0bcdc0ad.png" width="500">

Список доступных индикаторов (`enum Indicators`): <br>
`HEALTH` индикатор здоровья над инвентарем, <br>
`EXP` индикатор уровня, <br>
`HUNGER`индикатор голода игрока, <br>
`ARMOR` индикатор брони, <br>
`VEHICLE` индикатор здоровья управляемого моба (свинья, лощадь...), <br>
`TAB` список игроков при нажатии TAB<br>
`HOT_BAR` 9 слотов инвенторя игрока снизу экрана<br>
`AIR_BAR` воздух под водой<br>
`POTIONS` активные эффекты зелий<br>
`HAND` правая рука<br>
`NAME_TEMPLATE` ники игроков<br>

Методы взаимодействия с клиентом:<br>
`Anime.hideIndicator(player: Player, vararg indicator: Indicators)` скрывает игроку указанные через запятую
индикаторы<br>
`Anime.showIndicator(player: Player, vararg indicator: Indicators)` показывает игроку указанные через запятую
индикаторы<br>

<h3>Сферы STANDARD</h3>

<img alt="Сфера" src="https://i.imgur.com/z3eNuZZ.png" width="500"/>

Методы взаимодействия с клиентом:<br>
`Anime.sphere(to: Player, uuid: UUID, location: Location, color: Color, radius: Double)` создать сферу по указанным
координатам с указанным радиусом<br>
`Anime.sphere(to: Player, uuid: UUID, location: Location, color: Color, sX: Double, sY: Double, sZ: Double)` создать
сферу по указанным координатам с указанными размерами<br>
`Anime.teleportSphereTo(to: Player, uuid: UUID, location: Location)` телепортировать сферу<br>
`Anime.removeSphere(to: Player, uuid: UUID)` удалить сферу<br>
`Anime.moveSphereTo(to: Player, uuid: UUID, location: Location, time: Double)` плавно переместить сферу по указанным
координатам за указанное количество секунд<br>

<h3>Мультичат MULTICHAT</h3>

<img src="https://user-images.githubusercontent.com/73703007/180320456-ba52de50-258e-40df-bcab-3579f7947e97.png" width="500">

```kotlin
val chat = ModChat(
    UUID.randomUUID(), //Id чата
    "Системный чат", //Название
    "С" //Символ на кнопке (можно несколько символов)
)

MultiChat.createKey("system", chat) //Делаем удобный ключ для использования

//Делаем обработку сообщений
MultiChat.registerHandler(chat) { player, message ->
    player.sendMessage("Ты отправил в этот чат: $message")
}

MultiChat.sendChats(player, "system") //Отправляем игроку чат

MultiChat.sendMessage(player, "system", "Ты крутой сынок!") //Отправляем игроку сообщение

MultiChat.removeChats(player, "system") //Удаляем у игрока чат
```

Методы взаимодействия с клиентом:<br>
`MultiChat.createKey(id: String, chat: ModChat)` создать ключ для удобного использования чата<br>
`MultiChat.removeKey(chat: ModChat)` удалить ключ<br>
`MultiChat.removeKey(key: String)` удалить ключ по ключу чата<br>
`MultiChat.removeKey(id: UUID)` удалить ключ по id чата<br>
`MultiChat.registerHandler(id: UUID, consumer: BiConsumer<Player, String>)` добавить обработчик для сообщений, которые приходят в чат по id чата<br>
`MultiChat.registerHandler(key: String, consumer: BiConsumer<Player, String>)` добавить обработчик для сообщений, которые приходят в чат по ключу чата<br>
`MultiChat.sendChats(player: Player, vararg chats: ModChat)` отправить игроку сообщение все указанные чаты<br>
`MultiChat.sendChats(player: Player, vararg chats: UUID)` отправить игроку сообщение все указанные чаты по их id<br>
`MultiChat.sendChats(player: Player, vararg chats: String)` отправить игроку сообщение все указанные чаты по их ключу<br>
`MultiChat.removeChats(player: Player, vararg chats: ModChat)` удалить игроку все указанные чаты<br>
`MultiChat.removeChats(player: Player, vararg chats: UUID)` удалить игроку все указанные чаты по их id<br>
`MultiChat.removeChats(player: Player, vararg chats: String)` удалить игроку все указанные чаты по их ключу<br>
`MultiChat.sendMessage(player: Player, chat: ModChat, message: String)` отправить игроку сообщение в чат<br>
`MultiChat.sendMessage(player: Player, chat: UUID, message: String)` отправить игроку сообщение в чат по id чата<br>
`MultiChat.sendMessage(player: Player, chat: String, message: String)` отправить игроку сообщение в чат по ключу чата<br>
`MultiChat.broadcast(players: Collection<Player>, chat: ModChat, message: String)` отправить указанным игрокам сообщение в чат<br>
`MultiChat.broadcast(chat: ModChat, message: String)` отправить всем игрокам на сервере сообщение в чат<br>
`MultiChat.broadcast(players: Collection<Player>, chat: UUID, message: String)` отправить указанным игрокам сообщение в чат по id чата<br>
`MultiChat.broadcast(chat: UUID, message: String)` отправить всем игрокам на сервере сообщение в чат по id чата<br>
`MultiChat.broadcast(players: Collection<Player>, chat: UUID, message: String)` отправить указанным игрокам сообщение в чат по id чата<br>
`MultiChat.broadcast(chat: UUID, message: String)` отправить всем игрокам на сервере сообщение в чат по id чата<br>
`MultiChat.broadcast(players: Collection<Player>, chat: String, message: String)` отправить указанным игрокам сообщение в чат по ключу чата<br>
`MultiChat.broadcast(chat: String, message: String)` отправить всем игрокам на сервере сообщение в чат по ключу чата<br>

<h3>Подсвечивание границ экрана, виньетка (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913800-298a8e7b-22bf-4000-a03f-f32891459b63.png" width="500">

Цвета:

1. Цвета состоят из трез целых чисел и одного дробного, красный [0 .. 255], синий [0 .. 255], зеленый [0 .. 255],
   прозрачность [0 .. 1.0]
2. Вы можете еспользовать `enum GlowColor` с готовыми частыми цветами или создать свой `Tricolor()`

Методы взаимодействия с клиентом:<br>
`Glow.set(player: Player, red: Int, blue: Int, green: Int, alpha: Double)` ставит игроку свечение <br>
`Glow.set(player: Player, color: RGB)` то же самое, но через RGB<br><br>
`Glow.animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int, alpha: Double)` плавное появление и
скрытие свечения с указанием длительности этой анимации<br>
`Glow.animate(player: Player, seconds: Double, color: RGB, alpha: Double)` то же самое, но через RGB<br>
`Glow.animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int)` то же самое, но без указания
прозрачности (без прозрачности)<br>
`Glow.animate(player: Player, seconds: Double, color: RGB)` то же самое, но через RGB<br>

<h3>Подсвечивание границ места в мире EXPERIMENTAL</h3>

<img src="https://user-images.githubusercontent.com/42806772/146236013-43fbd0d1-4aa3-4a8c-b0a2-2ed37260575a.png" width="500">

Методы добавления места в глобальный список мест (не показывает игрокам!):<br>
`Glow.addPlace(red: Int, blue: Int, green: Int, x: Double, y: Double, z: Double, onJoin: (Player) -> Unit)`<br>
`Glow.addPlace(red: Int, blue: Int, green: Int, x: Double, y: Double, z: Double)`<br>
`Glow.addPlace(color: RGB, x: Double, y: Double, z: Double, onJoin: (Player) -> Unit)`<br>
`Glow.addPlace(color: RGB, x: Double, y: Double, z: Double)`<br>
`Glow.addPlace(place: GlowingPlace, onJoin: Consumer<Player>)`<br>
`Glow.addPlace(place: GlowingPlace)`<br>

Методы показа мест игроку:<br>
`Glow.showPlace(player: Player, place: GlowingPlace)` показать игроку место (место может быть не глобальным!)<br>
`Glow.showLoadedPlace(player: Player, uuid: UUID)` показать игрку глобальное место по UUID<br>
`Glow.showAllPlaces(player: Player)` показать игроку все глобальные места<br>

Очистка мест:<br>
`Glow.removePlace(place: GlowingPlace, vararg players: Player)` удаление места для указанных игроков и удаление из
списка глобальных<br>
`Glow.clearPlaces(vararg players: Player)` очистить указанным игрокам места и удалить все места их глобального
списка<br>

<h3>Оверлей Бустеров (модуль EXPERIMENTAL) </h3>

<img src="https://user-images.githubusercontent.com/63064550/181641989-22a7f776-1137-472d-8e55-5a8e468f82d7.png" width="500">

Показывает текущее состояние бустера игрока.

Пример для языка Kotlin:

```kotlin
val boosterBar = boosterBar {
    segments = listOf()
    title = ""
    subtitle = ""
    isShowBackground = false
    progress = 1.0
}

boosterBar.open(player)
```

<h3>Индикатор бустеров</h3>

<img src="https://raw.githubusercontent.com/Transiented/src/main/kjhqDxG6wwA.jpg" width="500">

Показывает текущее состояние бустера игрока.

Метод:<br>

`Anime.startBoosters(player: Player, vararg boosters: Booster)` показать индикатор<br>

<h3>Перезарядка над ActionBar (модуль EXPERIMENTAL)</h3>

<img src ="https://user-images.githubusercontent.com/63064550/149799259-9129ba80-b2a8-4553-9638-666b5ba950ab.png" width="500">

Методы:<br>

`Anime.reload(player: Player, seconds: Int, text: String)` простая перезарядка, с розовым цветом<br>
`Anime.reload(player: Player, seconds: Int, text: String, color: RGB, alpha: Double)` с использованием
RGB<br>
`Anime.reload(player: Player, seconds: Int, text: String, red: Int, green: Int, blue: Int, alpha: Double)` с любым
цветом в RGB<br>

<h3>Информация справа снизу (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/148701775-bd2afe54-b659-492a-b779-061269924130.png" width="500">

Методы:<br>
Устаревший метод, который выводит сообщение только в правом нижнем углу:
`Anime.bottomRightMessage(player: Player, text: String)` поставить справа снизу информацию игроку (строка делится
по `\n`)<br>
`Anime.bottomRightMessage(player: Player, vararg text: String)` тоже самое, но удобнее, так как можно указать строки
через запятую<br>

Новый метод, который выводит в 4 разных угла:
`Anime.overlayText(player: Player, position: Position, text: String)` позволяет размещать текст в разные стороны экрана (строка делится
по `\n`)<br>
`Anime.overlayText(player: Player, position: Position, vararg text: String)` тоже самое, но удобнее, так как можно указать строки
через запятую<br>

<h3>Искуственные игроки NPC</h3>

<img src="https://user-images.githubusercontent.com/42806772/147400304-4ffc0399-8fa2-4ef1-8346-ac0ad7e18ab8.gif" width="500">

Методы:<br>
`Npc.npc(init: NpcData.() -> Unit): NpcSmart` kotlin вариант конструктора для создания NPC (только через него можно
сделать глобальных NPC, которых плагин отправляет игроку при заходе на сервер)<br>
`Npc.npc(data: NpcData): NpcSmart` java версия (делает тоже самое)<br>
`Npc.spawn(entityId: Int)` показать всем игрокам заранее созданный NPC по entityId (оно есть в NpcData)<br>
`Npc.kill(entityId: Int)` спрятать всем игрокам заранее созданный NPC по entityId (оно есть в NpcData)<br>
`Npc.hide(entityId: Int, player: Player)` скрыть конкретному игроку NPC по entityId<br>
`Npc.show(entityId: Int, player: Player)` показать конкретному игроку NPC по entityId<br>
`Npc.clear()` удалить всем игрокам всех NPC (не чистит глобальный список NPC)<br>
NpcSmart:<br>

```kotlin
var data: NpcData, // данные на клиенте
var click: Consumer<PlayerUseUnknownEntityEvent>? = null, // обработчик нажатия на NPC
private var leftArm: ItemStack? = null, // предмет в левой руке
private var rightArm: ItemStack? = null, // предмет в правой руке
private var head: ItemStack? = null, // предмет на голове
private var chest: ItemStack? = null, // предмет на груди
private var legs: ItemStack? = null, // предмет на ножках
private var feet: ItemStack? = null, // предмет на стопах
```

`NpcSmart.slot(slot: EquipmentSlot, itemStack: ItemStack): NpcSmart` метод обновляющий слот NPC для всех игроков<br>
`NpcSmart.slot(slot: EquipmentSlot, itemStack: ItemStack, player: Player): NpcSmart` метод обновляющий слот NPC для
конкретного игрока<br>
`NpcSmart.kill(): NpcSmart` скрыть всем игрокам этого NPC<br>
`NpcSmart.show(player: Player): NpcSmart` показать игроку этого NPC<br>
`NpcSmart.hide(player: Player): NpcSmart` скрыть игроку этого NPC<br>
`NpcSmart.update(player: Player): NpcSmart` обновить игроку данные NPC (имя, координаты, поворот головы, положение тела

- сидеть, спать, шифтить)<br>
  `NpcSmart.swingArm(mainHand: Boolean, player: Player): NpcSmart` пошевелить рукой, указав главная ли рука и
  игрока-получателя<br>
  `NpcSmart.spawn(player: Player): NpcSmart` создать игроку NPC <--- Только тут игроку создается с нуля NPC<br>
  NpcData:<br>

```kotlin
var id: Int = (Math.random() * Int.MAX_VALUE).toInt(), // entityId NPC
val uuid: UUID = UUID.randomUUID(), // UUID
var x: Double = 0.0,
var y: Double = 0.0,
var z: Double = 0.0,
var type: Int = 1000, // тип сущности 1000 - ИГРОК
var name: String? = null, // имя
var behaviour: NpcBehaviour = NpcBehaviour.NONE, // модель поведения NPC
var pitch: Float = 0f,
var yaw: Float = 0f,
var skinUrl: String? = null, // ссылка на скин 
var skinDigest: String? = null,
var slimArms: Boolean = false, // тонкие руки
var sneaking: Boolean = false, // шифтит
var sleeping: Boolean = false, // спит
val sitting: Boolean = false, // сидит
val activationDistance: Int = -1 // -1  = без ограничения по блоком, любое другое число большее, чем 0 - радиус, в котором начинает смотреть на игрока
```

`NpcData.onClick(init: Consumer<PlayerUseUnknownEntityEvent>)` при клике на NPC обработать событие<br>
`NpcData.location(location: Location)` указать координаты NPC через локацию<br>
Модели поведения:<br>

`NONE` - нет поведения
`STARE_AT_PLAYER` - следит за игроком
`STARE_AND_LOOK_AROUND` - смотрит на игрока

Пример на Kotlin:<br>

```kotlin
npc {
    x = 1337.0
    y = 225.5
    z = -102.3
    behaviour = NpcBehaviour.STARE_AND_LOOK_AROUND
    onClick {
        it.player.sendMessage("Хай")
    }
}.slot(EquipmentSlot.HAND, CraftItemStack.asNMSCopy(ItemStack(Material.DIAMOND_BLOCK))).spawn()
```

<h3>Диалоги DIALOGS</h3>

<img src="https://user-images.githubusercontent.com/42806772/144916818-ff974368-878c-483d-a34b-79e3f2e576c8.png" width="500">

Методы взаимодействия с клинтом:<br>
`Dialog.sendDialog(player: Player, dialog: Dialog)` отправляет игру ветку диалогов<br>
`Dialog.openDialog(player: Player, dialogId: String)` показывает игроку диалог по ID ветки<br>
`Dialog.dialog(player: Player, dialog: Dialog, openEntrypoint: String)` отправляет и показывает игроку диалог по ID
ветки<br>

Структура диалога:
`Dialog` состоит из набора точек входа `Entrypoint`, в одном хранятся `Screen`, это конкоетно что видит игрок - набор
строк и кнопок `Button` с действиями `Action`, например открытия нового окна. Набор Action вы можете увидить
в `enum Actions`

Пример кода:<br>

```java
new Dialog(new Entrypoint(
    "id",
    "название",
    new Screen(а
        "Первая видимая строка",
        "Вторая видимая строка"
    ).buttons(
        new Button("Имя кнопки").actions(
            new Action (Actions.COMMAND).command("/next"),
            new Action (Actions.CLOSE)
        ),
        new Button ("Войти в очередь").actions(
            Action("/hub"),
            new Action (Actions.CLOSE)
        )
    )
))
```

<h3>Меню ежедневных наград STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913475-3ea8a658-2630-45d4-94ad-b637dc2b8665.png" width="500">

Метод показа окна награды:<br>
`Anime.openDailyRewardMenu(player: Player, currentDayIndex: Int, vararg week: DailyReward)` показать меню игроку с
указанием текущего дня [0..6], а также с указанием наград за каждый день в
объекте `DailyReward(имя предмета, сам предмет)`, дневных наград должно быть всегда указано 7 штук

<h3>Уведомления (зависит от предустановленного всеми cristalix-socials)</h3>

<img src="https://user-images.githubusercontent.com/42806772/155223931-23d3cafe-1028-435f-aa97-6702226ec3d9.png" width="500">

Уведомления возникают у игрока справа сверху, можно отправлять несколько, уведомление содержит UUID отправителя, в нем
может быть максимум 2 кнопки. Данная возможность не зависит от модулей `animation-api`, мод предустановлен
через `bungeecord` всем игрокам (Исходники мода `cristalix-socials` не будут добавлены в данный репозиторий).
<br>
Для начала рассмотрим создание кнопок уведомлений:<br>
<br>

```kotlin
Alert.button( // метод возвращает кнопку
    "Принять", // сообщение на кнопке
    "/ok", // команда при нажатии
    GlowColor.GOLD, // цвет кнопки
    false, // после нажатия удалится ли эта кнопка
    true // после нажатия скрыть все уведомление
)
```

<br>
Всего есть два подхода к отправке уведомлений игроку:<br>
1. Генерация уведомления прямо на месте отправки<br>
2. Создание шаблона уведомления, затем замена контента под ситуацию, отправка<br>
<br>
Рассмотрим первый вариант:

```kotlin
Alert.send(
    player = player, // кому отослать
    text = string, // сообщение
    millis = long, // милисекунд до скрытия уведомления (1000 = 1 секунда)
    frontColor = glowColor, // внешний цвет
    backGroundColor = glowColor, // цвет фона
    chatMessage = string, // сообщение в чате
    buttons: notificationButton... // кнопки (максимум две) (vararg)
)
```

Рассмотрим второй вариант:

1. Создание шаблона уведомления<br>

```kotlin
Alert.put(
    "hello", // ключ
    NotificationData( // содержимое
        null, 
        "notify",
        "Привет %nick%,\nкак дела?",
        toRGB(GlowColor.GREEN), 
        toRGB(GlowColor.GREEN), 
        3000, 
        listOf(
            button("Принять", "/ok", GlowColor.GOLD) // создание кнопки
        ), 
        "Вам уведомление!"
    )
)
```

2. Получение шаблона, замена текста, отправка:<br>

```kotlin
Alert.find("hello") // найти шаблон по ключу
    .replace("%nick", player.name) // заменить placeholder
    .replace("как дела?", "как спалось?")
    .send(player) // отправить игроку
```

<h3>Лутбокс LOOTBOX</h3>

<img src="https://user-images.githubusercontent.com/42806772/144919787-00cdac9a-a01c-4c48-97af-62c2f8e5f8b5.png" width="500">

Метод показа дропа с лутбокса:<br>
`Anime.openLootBox(player: Player, vararg items: LootDrop)` открыть игроку лутбокс с указанием всего что выпало в виде
объектов `LootDrop(сам предмет, название предмета, <НЕ ОБЯЗАТЕЛЬНО ПО УМАЛЧАНИЮ COMMON> редкость)`

Редкости предметов:

```kotlin
enum class DropRare(val title: String, val color: String) {
    COMMON("Обычный", "§a"),
    RARE("Редкий", "§9"),
    EPIC("Эпический", "§5"),
    LEGENDARY("Легендарный", "§6"), ;

    fun getColored(): String {
        return "$color$title" // метод для получения окрашенного имени редкости
    }
}
```

<h3>Сообщения об окончании игры</h3>

<img src="https://user-images.githubusercontent.com/63064550/150874231-4958a0af-8652-4475-a014-f73d23504b30.png" width="500">

Список экранов окончания игр (`enum EndStatus`): <br>
`WIN` экран победы, <br>
`LOSE` экран поражения, <br>
`DRAW` экран ничьей, <br>

Методы вызова экрана окончания игры:<br>
`Anime.showEnding(player: Player, endStatus: EndStatus, key: String, value: String)` показывает экран окончания игроку,
с информацией. `key` - сообщения слева, `value` - сообщения справа. Используйте `\n \n` - для переноса строки.<br>
`Anime.showEnding(player: Player, endStatus: EndStatus, key: List<String>, value: List<String>)` показывает экран
окончания игроку. `key` - список строк слева, `value` - список строк справа.

Пример использования:<br>

```kotlin
Anime.showEnding(
  player, 
  EndStatus.WIN, 
  listOf("Убийств:", "Смертей:"), 
  listOf("${user.kills}", "${user.deaths}")
)
```

Указаниие более чем 5 полей приведёт к нечитабельности статистки!

<h3>Маркеры в мире STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144920685-cf487bdc-ff08-4c19-9429-d54050d1bc32.png" width="500">

!!! ВАЖНО !!!<br>
Это частный случай Banners, класс помечен `@Deprecated`, новый инструмент способен делать намного больше, рекомендуем
испольовать его<br>

Что такое маркер? Это случайный UUID генерируемый в конструкторе, координаты в текущем мире игрока x, y, z, размер
текстуры и путь к текстуре на клиенте (обычно через resource-pack, но можно загрузить игроку текстуру через метод в
разделе "Прочее"). Для упращения работы с текстурами, вынесено несколько базовых в `enum MarkerSign`:

```kotlin
FINE("textures/others/znak_v_3.png")
ERROR("textures/others/znak_v_2.png")
WARNING("textures/others/znak_v_1.png")
QUESTION_FINE("textures/others/z1.png")
QUESTION_ERROR("textures/others/z2.png")
QUESTION_WARNING("textures/others/z1.png")
ARROW_DOWN("mcpatcher/cit/others/badges/arrow_down.png")
ARROW_UP("mcpatcher/cit/others/badges/arrow_up.png")
ARROW_RIGHT("mcpatcher/cit/others/badges/arrow_right.png")
ARROW_LEFT("mcpatcher/cit/others/badges/arrow_left.png")
```

Пример маркера: `new Marker(225, 1, 5, 10, MarkerSign.ERROR)`

Методы взаимодействия с клинтом:<br>
`Anime.marker(player: Player, marker: Marker)` показать маркер игроку<br>
`Anime.markers(player: Player, vararg markers: Marker)` показать игроку кучу маркеров<br>
<br>
`Anime.clearMarkers(player: Player)` очистить все маркеры у игрока<br>
`Anime.removeMarker(player: Player, marker: Marker)` удалить игроку маркер по маркеру<br>
`Anime.removeMarker(player: Player, uuid: UUID)` удалить игроку маркер по UUID маркера<br>
<br>
`Anime.moveMarker(player: Player, marker: Marker)` обновить нахождение маркера у игрока<br>
`Anime.moveMarker(player: Player, marker: Marker, seconds: Double)` обновить нахождение маркера у игрока анимированно за
указанное время<br>
`Anime.moveMarker(player: Player, uuid: UUID, toX: Double, toY: Double, toZ: Double, seconds: Double)` обновить
нахождение маркера у игрока анимированно за указанное время имея только UUID маркера<br>

<h3>Всплывающие сообщения STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144923669-6c52ffef-9359-4175-be7c-d6ead8d4ef3f.png" width="500">

`Anime.topMessage(player: Player, message: String)` сообщение сверху (поддерживает цвета и многострочные сообщения)<br>

<img src="https://user-images.githubusercontent.com/42806772/144923731-bb1d8a58-eb6d-4bd8-97e1-038cf3b5cda4.png" width="500">

`Anime.title(player: Player, text: String)` многострочный title, поддерживает все цвета, а так же `\n`<br>
`Anime.title(player: Player, vararg text: String)` то же самое, но строчк можно указывать через запятую или можно
передавать массив<br>

<img src="https://user-images.githubusercontent.com/42806772/144923540-7f2822be-efc1-4f1e-9e34-b4f3e1e704b4.png" width="500">

`Anime.alert(player: Player, title: String, description: String, seconds: Double)` отправить сообщение с комментарием с
указанием времени<br>
`Anime.alert(player: Player, title: String, description: String)` отправить сообщение с комментарием (на 7.3
секунды)<br>
`Anime.alert(player: Player, description: String, seconds: Double)` отправить отписание с сообщением "Внимание" с
указанием вреени<br>
`Anime.alert(player: Player, description: String)` комментарий с сообщением "Внимание" на 7.3 секунды<br>

<img src="https://user-images.githubusercontent.com/42806772/144923936-a99a7103-7ac3-4dcd-b78a-462358382c20.png" width="500">

`Anime.cursorMessage(player: Player, message: String)` сообщения появляющиеся на курсоре и летящие вниз<br>

<img src="https://user-images.githubusercontent.com/42806772/144924621-7f1d87ae-f0df-4943-af8f-18324cb99124.gif" width="500">

`Anime.itemTitle(player: Player, item: ItemStack, title: String?, subtitle: String?, duration: Double)` отправить на
экран предмет с указанием продолжительности в секундах (можно указать сообщения сверху и снизу)<br>

<img src="https://user-images.githubusercontent.com/42806772/144925126-c524e4e4-2cc5-45d1-8647-e5cf98301152.gif" width="500">

`Anime.counting321(player: Player)` начать на экране игрока отсчет 3 2 1 GO!<br>

<img src="https://i.imgur.com/sF9mxpL.png" width="500">

`Anime.bigAlert(player: Player, message: String)` отправляет текст на экран над хотбаром

<h3>Системная информация STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144923255-bb93a4eb-0ba8-4e1e-bf78-0c96332afcac.png" width="500">

`Anime.killboardMessage(player: Player, text: String)` отправить в правую верхнюю часть экрана сообщение<br>
`Anime.killboardMessage(player: Player, text: String, topMargin: Int)` отправить в правую верхнюю часть экрана сообщение
с указанием отсупа сверху<br>

<img src="https://user-images.githubusercontent.com/42806772/144923338-130365f6-bea4-4d4d-a9d1-8ec8d90dd7ad.png" width="500">

`Anime.timer(player: Player, text: String, duration: Int, red: Int, blue: Int, green: Int)` начать игроку отчсет сверху
с сообщением, длительностью, цветом полоски<br>
`Anime.timer(player: Player, text: String, duration: Int)` начать отсчет сверху с сообщением и продолжительностью<br>
`Anime.timer(player: Player, duration: Int)` начать отсчет с сообщением по умолчанию<br>

<img src="https://i.imgur.com/sp1U6Rv.png" width="500">

Есть 3 типа сообщений: FINE, WARN, ERROR. Каждое сообщение имеет свой цвет и символ в начале
`Anime.systemMessage(player: Player, messageStatus: MessageStatus, text: String)` отправить игроку системное сообщение

<h3>Прочее STANDARD</h3>

<img src="https://user-images.githubusercontent.com/42806772/144923420-56720196-99c3-4bc1-8fa1-597971e05a3c.png" width="500">

`Anime.corpse(to: Player, name: String?, uuid: UUID, x: Double, y: Double, z: Double, secondsAlive: Int = 60)` создать
труп по UUID трупа игрока с Cristalix и координат локации (имя трупу указывать необязательно) (secondsAlive - количество
секунд через которое исчезнет труп)<br>
`Anime.corpse(to: Player, name: String?, skinUrl: String, x: Double, y: Double, z: Double, secondsAlive: Int = 60)` то
же самое, но можно ставить скин трупу по любой ссылке (имя трупу указывать необязательно)<br>
`Anime.clearAllCorpses(player: Player)` очистить игроку все трупы
<br><br>
   
<b>Закрыть игроку активное меню (кроме настроек и меню ачивок)</b>:<br>

`Anime.close(player: Player)`<br>
   
<b>Отправить пустой Buffer игроку в канал</b>:<br>

`Anime.sendEmptyBuffer(channel: String, player: Player)`<br>

<b>Персонализация:</b><br>

`Anime.lockPersonalization(player: Player)` заблокировать персонализацию игроку<br>
`Anime.unlockPersonalization(player: Player)` разблокировать персонализацию игроку<br>

<b>Загрузка фотографий</b>:

`Anime.loadTexture(player: Player, url: String)` загрузить игроку фотографию по ссылке в папку `cache/animation/`<br>
`Anime.loadTextures(player: Player, vararg url: String)` загрузить игроку фотографии по ссылкам в
папку `cache/animation/`<br>
Когда игрок скачает фотографию/и от отправит серверу пустой буффер в канал `func:loaded` <br>
Пример получения фото на моде: `ResourceLocation.of("cache/animation", "файл.png")`

<b>Регистрация канала для входящих сообщений</b>:

`createReader(channel: String, listener: PluginMessageListener)`
