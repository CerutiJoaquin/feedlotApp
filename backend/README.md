
ANOTACIONES EN CLASES
/**
* @OneToMany indica que un animal puede tener varios pesajes.
* mappedBy: indica que la relación se mapea por el campo animal en la otra clase.
* cascade = CascadeType.ALL: significa que cualquier operación que se haga sobre Animal (guardar, eliminar, actualizar),
* se propagará a la lista relacionada.
*/

CLASE REPOSITORY

JpaRepository<Animal, Integer>:
Animal: la entidad que maneja.
Integer: el tipo del ID (clave primaria).
@Repository: marca la clase como componente de acceso a datos.
Métodos personalizados:
Spring genera automáticamente la implementación con solo definir el nombre.