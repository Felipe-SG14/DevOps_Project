<?php

//if(!isset($_GET["dispositivo_id"]))
//{
//    echo "Error en la url";
//}

//Profe
$conn = mysqli_connect('localhost', 'u447795502_pedro_tsm','WB>CFYs+3u', 'u447795502_2022_tsm');
//Jose
//$conn = mysqli_connect('localhost', 'id17880857_rotjeot','n8O\%t%sI}ImZy<q', 'id17880857_tsmdevops');
//Juan
//$conn = mysqli_connect('localhost','id18851704_user1','hSDAeD9*K|8Bqdmx','id18851704_spiraldatabase');

$sql = "SELECT * FROM FOCO ";

$query = $conn->query($sql);


if(mysqli_connect_errno())
{
    echo "error al conectar";
}
 if($query)
 {
    //$result = mysqli_fetch_row($query);
    
    while ($fila = $query->fetch_row()) {
        printf ("%s, %s,%s,\n", $fila[0], $fila[1],$fila[2]);
    }
 }
 else
 {
    echo "error al ejecutar query";
 }
 
 mysqli_close($conn);
 
 ?>
