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

if(isset($datos["dispositivo_id"]) && isset($datos["Intensidad"]))
{
    echo "Existe información\n";
}
else
{
    echo "Error\n";
}
$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');

//$conn = mysqli_connect('localhost','id18851704_user1','hSDAeD9*K|8Bqdmx','id18851704_spiraldatabase');
//$sql = "INSERT INTO `pokemones` (`Nombre`, `Tipo`) VALUES ('{$datos["nombre"]}', '{$datos["tipo"]}')";

$sql = "UPDATE FOCO SET Intensidad = '{$datos["Intensidad"]}' WHERE dispositivo_id = '{$datos["dispositivo_id"]}' ";

if(mysqli_connect_errno())
{
    echo "Error al conectar\n";
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