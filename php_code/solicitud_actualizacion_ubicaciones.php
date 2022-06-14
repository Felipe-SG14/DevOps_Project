<?php
//ip_host localhost
//usuario id17880857_rotjeot
//contraseÃ±a n8O\%t%sI}ImZy<q
//Base de datos id17880857_tsmdevops

/*
ip_host localhost
usuario u447795502_pedro_tsm
contraseÃ±a WB>CFYs+3u
Base de datos u447795502_2022_tsm
*/



$datos = json_decode(file_get_contents('php://input'), true);

print_r($datos);

if(isset($datos["USUARIO_ID"]))
{
    echo "Existe informacion\n";
}
else
{
    echo "Error\n";
}

$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');

$sql = "UPDATE UBICACION SET LONGITUD = '{$datos["LONGITUD"]}', LATITUD = '{$datos["LATITUD"]}' WHERE USUARIO_ID = '{$datos["USUARIO_ID"]}' ";

if(mysqli_connect_errno())
{
    echo "Error al conectar ubicaciÃ³n\n";
}

else{
    echo "Ã©xito conexiÃ³n\n";
}
 
 if(mysqli_query($conn, $sql ))
 {
     echo "Actualizado\n";
 }
 else
 {
    echo "Error al ejecutar, comprueba la informaciÃ³n\n";
 }
 
 mysqli_close($conn);
 
 ?>