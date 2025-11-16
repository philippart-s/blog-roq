---
title: "👹 Migrer son blog Jekyll vers Quarkus Roq ⚡️"
description: Comment et pourquoi migrer son blog Jekyll vers Quarkus Roq
image: 
tags: Code, blog
layout: :theme/post
category: Blog
author: wildagsx
---
# Migration

## Front matter
 - title --> title
 - classes --> ?
 - excerpt --> description
 - categories --> category
 - tags --> tags

## Images
 - copie dans le répertoire local de l'article
 - copyright image titre --> ?

## Contenu
 - remplacer `{:target="_blank"}` par rien
 - numéro de lignes dans le source --> ?
 - coloration syntaxique lisible --> ?

## Data
 - un seul fichier (ne peut pas lister les fichiers)
 - pas de moyen de lister les clefs racines -> retrait et utilisation de title
 - pas de champs avec - comme post-date --> notation UpperCamelCase `postDate`
 - pas possible d'utilise le terme `abstract` --> `pitch`