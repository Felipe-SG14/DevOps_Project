<?php
//ip_host localhost
//usuario id17880857_rotjeot
//contraseña n8O\%t%sI}ImZy<q
//Base de datos id17880857_tsmdevops


if(!isset($_GET["USUARIO_ID"]))
{
    echo "Error en la url";
}


$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');

//$conn = mysqli_connect('localhost', 'id17880857_rotjeot','n8O\%t%sI}ImZy<q', 'id17880857_tsmdevops');

$sql = "SELECT CLOSEHOME FROM UBICACION WHERE USUARIO_ID ='{$_GET["USUARIO_ID"]}' ";

$query = $conn->query($sql);


if(mysqli_connect_errno())
{
    echo "error al conectar";
}

 
 if($query)
 {
    $result = mysqli_fetch_assoc($query);
    $resultstring = $result['CLOSEHOME'];
    echo $resultstring;     
 }
 else
 {
    echo "error al ejecutar query";
 }
 
 mysqli_close($conn);
 
 ?>
