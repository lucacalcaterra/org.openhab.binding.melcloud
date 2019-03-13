# MelCloud Binding

This is an Openhab 2 binding for Mitsubishi MelCloud (https://www.melcloud.com/). 
Installing this binding you can control your Mitsubishi devices from Openhab without accessing the Melcloud App 
and benefiting from all openhab automations.

Project and Doc is work in progress, so i'm searching for people that help me with code and, in case, update Documentation.

## Supported Things

Supports mitsubishi air conditioners (tested with a couple of models I own like Kirigamine) automatically identified by Discovery after configuring the bridge that accesses the cloud.

## Discovery

Discovery automatically identify your devices on melcloud adn return them as Things.

## Binding Configuration

Nothing special for those who know Openhab.
Simply add MelCloud Bridge from PaperUI filling username and password  and then discovery do the rest.
Leave others parameters as they are; you can change the lang id (Default 0 for English) with you Language (
necessary to ensure that the dates are in the format to your area):

0   =   en  English
1   =   bg  Български
2   =   cs  Čeština
3   =   da  Dansk
4   =   de  Deutsch
5   =   et  Eesti
6   =   es  Español
7   =   fr  Français
8   =   hy  Հայերեն
9   =   lv  Latviešu
10  =   lt  Lietuvių
11  =   hu  Magyar
12  =   nl  Nederlands
13  =   no  Norwegian
14  =   pl  Polski
15  =   pt  Português
16  =   ru  Русский
17  =   fi  Suomi
18  =   sv  Svenska
19  =   it  Italiano
20  =   uk  Українська
21  =   tr  Türkçe
22  =   el  Ελληνικά
23  =   hr  Hrvatski
24  =   ro  Română
25  =   sl  Slovenščina

(thanks to il @Cato for this list) https://github.com/ilcato/homebridge-melcloud)

## Thing Configuration

No Thing Configuration for now.

## Channels

TODO: Add Doc for Thing's (A.C. Device) Channels

## Full Example for items configuration

TODO: Add Doc for manually configuring items .

## Issues

I am not a great java expert and since the binding is in beta testing we accept advice, reporting problems, or even better collaborations with experienced developers.

Author: Luca Calcaterra .
# NOTICE
This is not an official binding, and the author has no connection with Mitsubishi, nor has the company provided any information. The author made it only out of necessity and passion.
