# Le compte est bon

Le jeu du compte est bon sur Twitter, actuellement en ligne sur _[@CompteBon](https://twitter.com/CompteBon)_.

### Fonctionnement

Ce programme utilise plusieurs librairies dont :
- [twitter4s](https://github.com/DanielaSfregola/twitter4s) pour utiliser l'API Twitter
- [akka](https://github.com/akka/akka) pour le système d'acteurs
- [scala-parser-combinators](https://github.com/scala/scala-parser-combinators) pour parser les entrées des utilisateurs

Le règles détaillées du jeu se trouvent [ici](https://florian.cassayre.me/comptebon).


### Installation

Le programme se compile avec `sbt`.

Pour générer un paquet `.jar` standalone, il vous faudra exécuter la commande suivante :
```
sbt package
```
Le paquet devrait ensuite se situer dans le répertoire `target/scala_X.X.X/comptebon-assembly-X.X.X.jar`.

Il ne restera plus qu'à joindre un fichier de configuration contenant les deux paires d'identifiants nécessaire au fonctionnement de l'agent :
```
# production.conf

twitter {
  consumer {
    key = ""
    secret = ""
  }
  access {
    key = ""
    secret = ""
  }
}
```
Puis à joindre cette configuration lors de l'exécution :
```
java -Dconfig.file=production.conf -jar comptebon-assembly-X.X.X.jar
```


### Licence

Ce code est mis librement à disposition sous la licence MIT, se référer au fichier `LICENSE`.