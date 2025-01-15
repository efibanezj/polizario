# Polizario Project

Polizarion project.

## Installation

Use maven command:

```bash
mvn clean install
```
***
## Usage

```shell
java -jar polizarioProject-0.0.1.jar --spring.profiles.active=local
```
***
## API
* **Polizario API**: This API generated a consolidate file with information of ***polizario*** files.
  * Ex : POLIZARI.UG.F210511.CRS814.TXT
  ```
   GET http://localhost:8080/polizario/resume
    ```

* **Interfaz API**
This API generated a consolidate file with information of ***interface*** files. This is the result of the process execution by an internal applications.
    ```
    GET http://localhost:8080/accountant-interfaces/{type}/resume
    ```
  * Types of ***Interfaz*** files
    * **qh** : Files with " / " as separator.  Ex : INTERFAZ.UG.F1726.TXT
    * **no-qh** :  Files with " / " as separator.  

***
## Test files
 You can find examples to test in the following directory:
 ```dir
 /test/resources/files/
 ```


## License
[MIT](https://choosealicense.com/licenses/mit/)
