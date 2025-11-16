---
title: Conferences
layout: :theme/page
tags:
  - Conférences
  - Slides
  - Replays
---

Ici vous trouverez l'ensemble des conférences auxquelles j'ai participé et que j'ai présentées.

Vous pouvez aussi retrouver la [liste des sujets]({{ site.baseurl }}/talks) que j'ai donnés en conférence.

<style>
  table {
    width: 100%;
    height: 100%;
    display: table;
  }

  th,
  td {
    border: 0px solid #000;
    padding: 10px;
    text-align: center;
    vertical-align: middle;
  }

  th {
    background-color: #f2f2f2;
  }
</style>

<table>
  {#for conference in cdi:conferences.confs %}

  <tr>
    <th colspan="3">
      <h1>  {conference.name} </h1>
    </th>
  </tr>

  <tr>
    <td style="width: 35%; text-align: left;">
      {conference.name}
    </td>
    <td style="width: 35%;">
      🗓️ {conference.date} 🗓️
    </td>
    <td style="width: 30%;">
      🎤 <a href="{conference.name}">
        Liste des talks
      </a> 🎤
    </td>
  </tr>
  {/for}
</table>