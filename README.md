## Reasons

* Separation of `StageDef` from individual types isn't helping because the only non-optional field is name. Might as well be a property on a `Stage` base class and a required constructor arg. Have to discover both the `stage` method and the `Stage` concrete type.
