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

// Array hardcoded methods

procedure java.util.Arrays.copyOf$Ref$int(param00 : Ref, param01 : int) returns (r : Ref) {
    call r := Alloc();
    assume $ArrayLength(r) == param01;
}


// Collections' length mock

var $CollectionLength : [Ref]int;

// Collection methods

procedure java.util.Collection#size($this : Ref) returns (r : int) {
    r := $CollectionLength[$this];
}

procedure java.util.Collection#clear($this : Ref) {
    $CollectionLength[$this] := 0;
}

procedure java.util.Collection#remove$int($this : Ref, i : int) returns (r : bool) {
    $CollectionLength[$this] := $CollectionLength[$this] - 1;
    r := true;
}

procedure java.util.Collection#remove$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    $CollectionLength[$this] := $CollectionLength[$this] - 1;
    r := true;
}

procedure java.util.Collection#add$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    $CollectionLength[$this] := $CollectionLength[$this] + 1;
    r := true;
}

// List methods

procedure java.util.List#size($this : Ref) returns (r : int) {
    call r := java.util.Collection#size($this);
}

procedure java.util.List#clear($this : Ref) {
    call java.util.Collection#clear($this);
}

procedure java.util.List#remove$int($this : Ref, i : int) returns (r : bool) {
    call r := java.util.Collection#remove$int($this, i);
}

procedure java.util.List#remove$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#remove$java.lang.Object($this, o);
}

procedure java.util.List#add$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#add$java.lang.Object($this, o);
}

// ArrayList methods

procedure java.util.ArrayList#?init?($this : Ref) {
    $CollectionLength[$this] := 0;
}

procedure java.util.ArrayList#?init?$int($this : Ref, i : int) {
    $CollectionLength[$this] := i;
}

procedure java.util.ArrayList#size($this : Ref) returns (r : int) {
    call r := java.util.Collection#size($this);
}

procedure java.util.ArrayList#clear($this : Ref) {
    call java.util.Collection#clear($this);
}

procedure java.util.ArrayList#remove$int($this : Ref, i : int) returns (r : bool) {
    call r := java.util.Collection#remove$int($this, i);
}

procedure java.util.ArrayList#remove$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#remove$java.lang.Object($this, o);
}

procedure java.util.ArrayList#add$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#add$java.lang.Object($this, o);
}

// LinkedList methods

procedure java.util.LinkedList#?init?($this : Ref) {
    $CollectionLength[$this] := 0;
}

procedure java.util.LinkedList#size($this : Ref) returns (r : int) {
    call r := java.util.Collection#size($this);
}

procedure java.util.LinkedList#clear($this : Ref) {
    call java.util.Collection#clear($this);
}

procedure java.util.LinkedList#remove$int($this : Ref, i : int) returns (r : bool) {
    call r := java.util.Collection#remove$int($this, i);
}

procedure java.util.LinkedList#remove$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#remove$java.lang.Object($this, o);
}

procedure java.util.LinkedList#add$java.lang.Object($this : Ref, o : Ref) returns (r : bool) {
    call r := java.util.Collection#add$java.lang.Object($this, o);
}
