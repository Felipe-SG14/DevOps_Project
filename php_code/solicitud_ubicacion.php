<?php
//ip_host localhost
//usuario id17880857_rotjeot
//contraseña n8O\%t%sI}ImZy<q
//Base de datos id17880857_tsmdevops

/*
ip_host localhost
usuario u447795502_pedro_tsm
contraseña WB>CFYs+3u
Base de datos u447795502_2022_tsm
*/



$datos = json_decode(file_get_contents('php://input'), true);

print_r($datos);

if(isset($datos["UBICACION_ID"]) && isset($datos["LATITUD"]) && isset($datos["LONGITUD"]))
{
    echo "Existe informacion\n";
}
else
{
    echo "Error\n";
}

$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');
//$sql = "INSERT INTO `pokemones` (`Nombre`, `Tipo`) VALUES ('{$datos["nombre"]}', '{$datos["tipo"]}')";

$sql = "UPDATE UBICACION SET LONGITUD = '{$datos["LONGITUD"]}', LATITUD = '{$datos["LATITUD"]}' WHERE UBICACION_ID = '{$datos["UBICACION_ID"]}' ";

if(mysqli_connect_errno())
{
    echo "Error al conectar ubicación\n";
}

else{
    echo "éxito conexión\n";
}
 
 if(mysqli_query($conn, $sql ))
 {
     echo "Actualizado\n";
 }
 else
 {
    echo "Error al ejecutar, comprueba la información\n";
 }
 
 mysqli_close($conn);
 
 ?>