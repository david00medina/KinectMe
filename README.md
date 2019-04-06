# KinectME
## Descripción
Se hace uso de la _Kinect v1.8_ y sus sensores IR 
para implementar un prototipo de aplicación de realidad 
aumentada con profundidad.

![](doc/kinectme.gif)

## Estructura del proyecto
Este proyecto está estructurado en los siguientes directorios:
* data. Contiene los modelos utilizados en este proyecto
* doc. Contiene la documentación oficial de este proyecto en 
formato _PDF_. Se dispone además del código fuente del documento 
en _TEX_ y sus referencias en _BIB_.
* lib. Contiene todas las librerías utilizadas en el proyecto
* src. Contiene el código fuente del proyecto

## Ejecución del archivo _JAR_ compilado
Se dispone de un archivo binario llamado _KinectMe.jar_ que 
permite ejecutar el proyecto ya compilado. Se debe ejecutar el archivo
_KinectMe.jar_ dentro del directorio raíz del proyecto. 

Ejecutar el siguiente comando por consola:

  `java -jar -Djava.library.path=lib\kinect4winsdk KinectMe.jar`
  
