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
$email = $datos['CORREO'];
$pas = $datos['PASSWORD'];

//print_r($datos);


if(!isset($datos["CORREO"]) && !isset($datos["PASSWORD"]))
{
    echo "Error\n";
}

$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');
//$sql = "INSERT INTO `pokemones` (`Nombre`, `Tipo`) VALUES ('{$datos["nombre"]}', '{$datos["tipo"]}')";

$sql = "SELECT USUARIO_ID FROM `CUENTA` WHERE CORREO ='$email' AND PASSWORD = '$pas'";



$query = $conn->query($sql);


if(mysqli_connect_errno())
{
    echo "Error al conectar\n";
}

if($query)
{
   $myObj = new stdClass();
   $result = mysqli_fetch_assoc($query);
   $resultstring = $result['USUARIO_ID'];
   //$resultstrnigI = $result['Intensidad'];

   if($resultstring != null)
   {
    $myObj->ID = $resultstring;
    $file = json_encode($myObj);
    echo $file;
   }
   else
   {
    $myObj->ID = "0";
    $file = json_encode($myObj);
    echo $file;
   }
    
}
else
{
   echo "error al ejecutar query";

}

mysqli_close($conn);
 
 ?>