# Cristalix Arcade API DOCS (актуальная версия 1.0.7)

Это система объединяющая все аркады на Cristalix. 
Проект тянет за собой Animation API, и сам включает его, поэтому вам не нужно добавлять его отдельно.
Мультисерверные: валюта, префикс, лутбосы, донат (скоро)
<br>

<h2>Gradle Get Started</h2>

```
repositories {
  mavenLocal()
  mavenCentral()
  maven {
    url 'https://repo.implario.dev/cristalix/'
    credentials {
      username System.getenv("IMPLARIO_REPO_USER")
      password System.getenv("IMPLARIO_REPO_PASSWORD")
    }
  }
}

dependencies {
  implementation 'me.func:arcade-api:1.0.7'
  implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.6.0'
}
```

<h2>Важно</h2>

При завершении игры или при кике всех игроков нужно вызвать `Arcade.bulkSave(vararg players: Player)` чтобы сделать сохранение игроков в одном запросе, а не в отдельных

<h2>Методы</h2>

<h3>Основное</h3> 

`Arcade.getMoney(uuid: UUID)` получить баланс игрока по UUID<br>
`Arcade.getMoney(player: Player)` получить баланс по игроку<br>
<br>
`Arcade.setMoney(uuid: UUID, money: Long)` поставить баланс по UUID<br>
`Arcade.setMoney(player: Player, money: Long)` поставить баланс<br>
<br>
`Arcade.deposit(uuid: UUID, money: Int)` выдать игроку деньги по UUID (с сообщением на экране)<br>
`Arcade.deposit(player: Player, money: Int)` выдать игроку деньги<br>
<br>
`Arcade.openLootbox(uuid: UUID)` убрать игроку один лутбокс<br>
`Arcade.giveLootbox(uuid: UUID)` выдать игроку один лутбокс<br>
<br>
`Arcade.updatePrefix(uuid: UUID, prefix: String)` обновить мультисерверный префикс <br>

<h3>Дополнительное</h3>

`Arcade.getDonate(uuid: UUID): ArcadePlayer` получить весь донат игрока по UUID<br>
`Arcade.getDonate(player: Player): ArcadePlayer` получить весь донат игрока<br>

<h3>Весь донат</h3>

```
    var killMessage: KillMessage = KillMessage.NONE, // сообщения при убийстве
    var stepParticle: StepParticle = StepParticle.NONE, // частицы ходьбы
    var nameTag: NameTag = NameTag.NONE, // префикс
    var corpse: Corpse = Corpse.NONE, // магила после смерти
    var arrowParticle: ArrowParticle = ArrowParticle.NONE, // частицы стрелы
    var mask: Mask = Mask.NONE, // маска на голову
```
