type Ref;

type Field;

type Union = Ref;

const unique null: Ref;

var $Alloc: [Ref]bool;

procedure {:inline 1} Alloc() returns (x: Ref);
  modifies $Alloc;

implementation {:inline 1} Alloc() returns (x: Ref)
{
    assume $Alloc[x] == false && x != null;
    $Alloc[x] := true;
}

type HeapType = [Ref][Field]Union;

var $Heap: HeapType;

function {:inline true} Read(H: HeapType, o: Ref, f: Field) : Union
{
  H[o][f]
}

function {:inline true} Write(H: HeapType, o: Ref, f: Field, v: Union) : HeapType
{
  H[o := H[o][f := v]]
}

var {:thread_local} $Exception: Ref;

procedure division(a : int, b : int) returns (r : int) {
    if (b == 0) {
        call $Exception := Alloc();
        //TODO: Set exception's type
        return;
    }

    r := a div b;
}

var $ArrayContents: [Ref][int]Union;

function $ArrayLength(Ref) : int;

function Union2Bool(u: Union) : bool;

function Union2Int(u: Union) : int;

function Bool2Union(boolValue: bool) : Union;

function Int2Union(intValue: int) : Union;
