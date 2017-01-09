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

var HardcodedCollectionsMethodsCall : Ref;

var examples.ATM : Ref;

const unique examples.ATM#theCardIn : Field;

const unique examples.ATM#carHalfway : Field;

const unique examples.ATM#passwordGiven : Field;

const unique examples.ATM#card : Field;

const unique examples.ATM#passwd : Field;

var examples.FiniteStack : Ref;

const unique examples.FiniteStack#max : Field;

const unique examples.FiniteStack#next : Field;

var examples.GenericStack : Ref;

const unique examples.GenericStack#capacity : Field;

const unique examples.GenericStack#data : Field;

const unique examples.GenericStack#size : Field;

var examples.List : Ref;

const unique examples.List#size : Field;

var examples.ListIterator.MyArrayList : Ref;

const unique examples.ListIterator.MyArrayList#elementData : Field;

const unique examples.ListIterator.MyArrayList#size : Field;

var examples.ListIterator.MyListItr : Ref;

const unique examples.ListIterator.MyListItr#list : Field;

const unique examples.ListIterator.MyListItr#cursor : Field;

const unique examples.ListIterator.MyListItr#lastRet : Field;

var examples.PreconditionOnParametersTest : Ref;

var examples.Switch : Ref;

const unique examples.Switch#isOn : Field;

var examples.arrayList.ArrayList : Ref;

const unique examples.arrayList.ArrayList#elementData : Field;

const unique examples.arrayList.ArrayList#size : Field;

const unique examples.arrayList.ArrayList#modCount : Field;

var examples.arrayList.ListIterator : Ref;

const unique examples.arrayList.ListIterator#arrayList : Field;

const unique examples.arrayList.ListIterator#cursor : Field;

const unique examples.arrayList.ListIterator#lastRet : Field;

const unique examples.arrayList.ListIterator#expectedModCount : Field;

var examples.unannotated.GenericStack : Ref;

const unique examples.unannotated.GenericStack#capacity : Field;

const unique examples.unannotated.GenericStack#data : Field;

const unique examples.unannotated.GenericStack#size : Field;

var java.lang.IllegalArgumentException : Ref;

var java.lang.IllegalArgumentException.serialVersionUID : int;

var java.lang.IllegalStateException : Ref;

var java.lang.IllegalStateException.serialVersionUID : int;

var java.lang.IndexOutOfBoundsException : Ref;

var java.lang.IndexOutOfBoundsException.serialVersionUID : int;

var java.lang.Integer : Ref;

var java.lang.Integer.MIN_VALUE : int;

var java.lang.Integer.MAX_VALUE : int;

var java.lang.Integer.TYPE : Ref;

var java.lang.Integer.digits : Ref;

var java.lang.Integer.DigitTens : Ref;

var java.lang.Integer.DigitOnes : Ref;

var java.lang.Integer.sizeTable : Ref;

var java.lang.Integer.SIZE : int;

var java.lang.Integer.serialVersionUID : int;

var java.lang.Integer.$assertionsDisabled : bool;

const unique java.lang.Integer#value : Field;

var java.lang.Object : Ref;

var java.lang.RuntimeException : Ref;

var java.lang.RuntimeException.serialVersionUID : int;

var java.lang.StringBuilder : Ref;

var java.lang.StringBuilder.serialVersionUID : int;

var java.lang.System : Ref;

var java.lang.System.in : Ref;

var java.lang.System.out : Ref;

var java.lang.System.err : Ref;

var java.lang.System.security : Ref;

var java.lang.System.cons : Ref;

var java.lang.System.props : Ref;

var java.lang.System.lineSeparator : Ref;

var java.util.AbstractCollection : Ref;

var java.util.AbstractCollection.MAX_ARRAY_SIZE : int;

var java.util.ArrayList : Ref;

var java.util.ArrayList.serialVersionUID : int;

var java.util.ArrayList.DEFAULT_CAPACITY : int;

var java.util.ArrayList.EMPTY_ELEMENTDATA : Ref;

var java.util.ArrayList.MAX_ARRAY_SIZE : int;

const unique java.util.ArrayList#elementData : Field;

const unique java.util.ArrayList#size : Field;

var java.util.Arrays : Ref;

var java.util.Arrays.INSERTIONSORT_THRESHOLD : int;

var java.util.Arrays.$assertionsDisabled : bool;

var java.util.Collection : Ref;

var java.util.ConcurrentModificationException : Ref;

var java.util.ConcurrentModificationException.serialVersionUID : int;

var java.util.LinkedList : Ref;

var java.util.LinkedList.serialVersionUID : int;

const unique java.util.LinkedList#size : Field;

const unique java.util.LinkedList#first : Field;

const unique java.util.LinkedList#last : Field;

var java.util.List : Ref;

var java.util.NoSuchElementException : Ref;

var java.util.NoSuchElementException.serialVersionUID : int;

var stringConstant_396102794_Illegal_Capacity__ : Ref;

var stringConstant_687023272_Index__ : Ref;

var stringConstant_186512763___Size__ : Ref;

procedure initialize_globals() {
    $Exception := null;
    call java.util.Collection := Alloc();
    call examples.GenericStack := Alloc();
    call java.lang.RuntimeException := Alloc();
    call java.util.AbstractCollection := Alloc();
    call java.util.List := Alloc();
    call examples.ListIterator.MyArrayList := Alloc();
    call java.lang.IllegalArgumentException := Alloc();
    call HardcodedCollectionsMethodsCall := Alloc();
    call java.lang.StringBuilder := Alloc();
    call java.util.ConcurrentModificationException := Alloc();
    call java.lang.Object := Alloc();
    call examples.unannotated.GenericStack := Alloc();
    call examples.arrayList.ListIterator := Alloc();
    call examples.ATM := Alloc();
    call java.lang.IllegalStateException := Alloc();
    call java.lang.IndexOutOfBoundsException := Alloc();
    call java.util.Arrays := Alloc();
    call java.lang.System := Alloc();
    call examples.ListIterator.MyListItr := Alloc();
    call examples.FiniteStack := Alloc();
    call java.lang.Integer := Alloc();
    call java.util.ArrayList := Alloc();
    call examples.List := Alloc();
    call examples.Switch := Alloc();
    call examples.PreconditionOnParametersTest := Alloc();
    call java.util.LinkedList := Alloc();
    call java.util.NoSuchElementException := Alloc();
    call examples.arrayList.ArrayList := Alloc();
    call stringConstant_396102794_Illegal_Capacity__ := Alloc();
    call stringConstant_687023272_Index__ := Alloc();
    call stringConstant_186512763___Size__ := Alloc();
}

procedure HardcodedCollectionsMethodsCall#?init?($this : Ref)
{
    var r0 : Ref;
    

    HardcodedCollectionsMethodsCall#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure HardcodedCollectionsMethodsCall#method$java.util.List$java.util.LinkedList$java.util.ArrayList$java.util.Collection($this : Ref, r1 : Ref, r2 : Ref, r3 : Ref, r4 : Ref)
{
    var r0 : Ref;
    var r5 : Ref;
    var r6 : Ref;
    var r7 : Ref;
    var $r8 : Ref;
    var $r12 : Ref;
    var $r13 : Ref;
    var $r14 : Ref;
    var $ret$454305524 : bool;
    var $ret$1117519786 : bool;
    var $ret$282265585 : bool;
    var $ret$1249875355 : bool;
    var $ret$1297836716 : bool;
    var $ret$1048855692 : bool;
    var $ret$1212116343 : bool;
    var $ret$1237825806 : bool;
    var $ret$1671507048 : bool;
    var $ret$687059528 : bool;
    var $ret$1409545055 : bool;
    var $ret$2119891622 : bool;

    HardcodedCollectionsMethodsCall#method$java.util.List$java.util.LinkedList$java.util.ArrayList$java.util.Collection_0:
        r0 := $this;
        
        
        
        
        call java.util.Collection#clear(r4);
        if ($Exception != null) {
            return;
        }
        call $ret$1237825806 := java.util.Collection#add$java.lang.Object(r4, null);
        if ($Exception != null) {
            return;
        }
        call $r8 := java.lang.Integer.valueOf$int(1);
        if ($Exception != null) {
            return;
        }
        call $ret$282265585 := java.util.Collection#remove$java.lang.Object(r4, $r8);
        if ($Exception != null) {
            return;
        }
        call $ret$1297836716 := java.util.Collection#remove$java.lang.Object(r4, null);
        if ($Exception != null) {
            return;
        }
        call java.util.List#clear(r1);
        if ($Exception != null) {
            return;
        }
        call $ret$1048855692 := java.util.List#add$java.lang.Object(r1, null);
        if ($Exception != null) {
            return;
        }
        call $ret$1249875355 := java.util.List#remove$int(r1, 0);
        if ($Exception != null) {
            return;
        }
        call $ret$1117519786 := java.util.List#remove$java.lang.Object(r1, null);
        if ($Exception != null) {
            return;
        }
        call java.util.LinkedList#clear(r2);
        if ($Exception != null) {
            return;
        }
        call $ret$1409545055 := java.util.LinkedList#add$java.lang.Object(r2, null);
        if ($Exception != null) {
            return;
        }
        call $ret$1212116343 := java.util.LinkedList#remove$int(r2, 0);
        if ($Exception != null) {
            return;
        }
        call $ret$2119891622 := java.util.LinkedList#remove$java.lang.Object(r2, null);
        if ($Exception != null) {
            return;
        }
        call java.util.ArrayList#clear(r3);
        if ($Exception != null) {
            return;
        }
        call $ret$1671507048 := java.util.ArrayList#add$java.lang.Object(r3, null);
        if ($Exception != null) {
            return;
        }
        call $ret$687059528 := java.util.ArrayList#remove$int(r3, 0);
        if ($Exception != null) {
            return;
        }
        call $ret$454305524 := java.util.ArrayList#remove$java.lang.Object(r3, null);
        if ($Exception != null) {
            return;
        }
        call $r12 := Alloc();
        call java.util.LinkedList#?init?($r12);
        if ($Exception != null) {
            return;
        }
        r5 := $r12;
        call $r13 := Alloc();
        call java.util.ArrayList#?init?($r13);
        if ($Exception != null) {
            return;
        }
        r6 := $r13;
        call $r14 := Alloc();
        call java.util.ArrayList#?init?$int($r14, 4);
        if ($Exception != null) {
            return;
        }
        r7 := $r14;
        return;
    
}

procedure examples.ATM#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#theCardIn, Bool2Union(false));
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#carHalfway, Bool2Union(false));
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#passwordGiven, Bool2Union(false));
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.ATM#card, Int2Union(0));
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.ATM#passwd, Int2Union(0));
        return;
    
}

procedure examples.ATM#CanceledMessage($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#CanceledMessage_0:
        r0 := $this;
        return;
    
}

procedure examples.ATM#CanceledMessage_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#CanceledMessage_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#theCardIn));
        r := $z0;
        return;
    
}

procedure examples.ATM#DisplayMainScreen($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#DisplayMainScreen_0:
        r0 := $this;
        return;
    
}

procedure examples.ATM#DisplayMainScreen_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    var $z2 : bool;
    

    examples.ATM#DisplayMainScreen_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#theCardIn));
        if ($z0 != false) {
            goto examples.ATM#DisplayMainScreen_pre_3;
        }
    
    examples.ATM#DisplayMainScreen_pre_1:
        $z1 := Union2Bool(Read($Heap, r0, examples.ATM#carHalfway));
        if ($z1 != false) {
            goto examples.ATM#DisplayMainScreen_pre_3;
        }
    
    examples.ATM#DisplayMainScreen_pre_2:
        $z2 := true;
        goto examples.ATM#DisplayMainScreen_pre_4;
    
    examples.ATM#DisplayMainScreen_pre_3:
        $z2 := false;
    
    examples.ATM#DisplayMainScreen_pre_4:
        r := $z2;
        return;
    
}

procedure examples.ATM#EjectCard($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#EjectCard_0:
        r0 := $this;
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#theCardIn, Bool2Union(false));
        assume Union2Bool(Bool2Union(true)) == true;
        $Heap := Write($Heap, r0, examples.ATM#carHalfway, Bool2Union(true));
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.ATM#card, Int2Union(0));
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.ATM#passwd, Int2Union(0));
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#passwordGiven, Bool2Union(false));
        return;
    
}

procedure examples.ATM#EjectCard_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#EjectCard_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#theCardIn));
        r := $z0;
        return;
    
}

procedure examples.ATM#EnterPassword$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    

    examples.ATM#EnterPassword$int_0:
        r0 := $this;
        
        assume Union2Bool(Bool2Union(true)) == true;
        $Heap := Write($Heap, r0, examples.ATM#passwordGiven, Bool2Union(true));
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.ATM#passwd, Int2Union(i0));
        return;
    
}

procedure examples.ATM#EnterPassword_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    

    examples.ATM#EnterPassword_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#passwordGiven));
        if ($z0 != false) {
            goto examples.ATM#EnterPassword_pre_2;
        }
    
    examples.ATM#EnterPassword_pre_1:
        $z1 := true;
        goto examples.ATM#EnterPassword_pre_3;
    
    examples.ATM#EnterPassword_pre_2:
        $z1 := false;
    
    examples.ATM#EnterPassword_pre_3:
        r := $z1;
        return;
    
}

procedure examples.ATM#EnterPassword_pre$int($this : Ref, i0 : int) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#EnterPassword_pre$int_0:
        r0 := $this;
        
        if (i0 <= 0) {
            goto examples.ATM#EnterPassword_pre$int_2;
        }
    
    examples.ATM#EnterPassword_pre$int_1:
        $z0 := true;
        goto examples.ATM#EnterPassword_pre$int_3;
    
    examples.ATM#EnterPassword_pre$int_2:
        $z0 := false;
    
    examples.ATM#EnterPassword_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.ATM#InsertCard$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    

    examples.ATM#InsertCard$int_0:
        r0 := $this;
        
        assume Union2Bool(Bool2Union(true)) == true;
        $Heap := Write($Heap, r0, examples.ATM#theCardIn, Bool2Union(true));
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.ATM#card, Int2Union(i0));
        return;
    
}

procedure examples.ATM#InsertCard_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    

    examples.ATM#InsertCard_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#theCardIn));
        if ($z0 != false) {
            goto examples.ATM#InsertCard_pre_2;
        }
    
    examples.ATM#InsertCard_pre_1:
        $z1 := true;
        goto examples.ATM#InsertCard_pre_3;
    
    examples.ATM#InsertCard_pre_2:
        $z1 := false;
    
    examples.ATM#InsertCard_pre_3:
        r := $z1;
        return;
    
}

procedure examples.ATM#InsertCard_pre$int($this : Ref, i0 : int) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#InsertCard_pre$int_0:
        r0 := $this;
        
        if (i0 <= 0) {
            goto examples.ATM#InsertCard_pre$int_2;
        }
    
    examples.ATM#InsertCard_pre$int_1:
        $z0 := true;
        goto examples.ATM#InsertCard_pre$int_3;
    
    examples.ATM#InsertCard_pre$int_2:
        $z0 := false;
    
    examples.ATM#InsertCard_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.ATM#RequestPassword($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#RequestPassword_0:
        r0 := $this;
        return;
    
}

procedure examples.ATM#RequestPassword_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    

    examples.ATM#RequestPassword_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#passwordGiven));
        if ($z0 != false) {
            goto examples.ATM#RequestPassword_pre_2;
        }
    
    examples.ATM#RequestPassword_pre_1:
        $z1 := true;
        goto examples.ATM#RequestPassword_pre_3;
    
    examples.ATM#RequestPassword_pre_2:
        $z1 := false;
    
    examples.ATM#RequestPassword_pre_3:
        r := $z1;
        return;
    
}

procedure examples.ATM#RequestTakeCard($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#RequestTakeCard_0:
        r0 := $this;
        return;
    
}

procedure examples.ATM#RequestTakeCard_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#RequestTakeCard_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#carHalfway));
        r := $z0;
        return;
    
}

procedure examples.ATM#TakeCard($this : Ref)
{
    var r0 : Ref;
    

    examples.ATM#TakeCard_0:
        r0 := $this;
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#carHalfway, Bool2Union(false));
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.ATM#theCardIn, Bool2Union(false));
        return;
    
}

procedure examples.ATM#TakeCard_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.ATM#TakeCard_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#carHalfway));
        r := $z0;
        return;
    
}

procedure examples.ATM#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    var $z2 : bool;
    

    examples.ATM#inv_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.ATM#theCardIn));
        if ($z0 == false) {
            goto examples.ATM#inv_2;
        }
    
    examples.ATM#inv_1:
        $z1 := Union2Bool(Read($Heap, r0, examples.ATM#carHalfway));
        if ($z1 != false) {
            goto examples.ATM#inv_3;
        }
    
    examples.ATM#inv_2:
        $z2 := true;
        goto examples.ATM#inv_4;
    
    examples.ATM#inv_3:
        $z2 := false;
    
    examples.ATM#inv_4:
        r := $z2;
        return;
    
}

procedure examples.FiniteStack#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.FiniteStack#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(5)) == 5;
        $Heap := Write($Heap, r0, examples.FiniteStack#max, Int2Union(5));
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union(-1));
        return;
    
}

procedure examples.FiniteStack#?init?$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    

    examples.FiniteStack#?init?$int_0:
        r0 := $this;
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.FiniteStack#max, Int2Union(i0));
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union(-1));
        return;
    
}

procedure examples.FiniteStack#Pop($this : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $i4 : int;
    

    examples.FiniteStack#Pop_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        $i1 := $i0 - 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union($i1));
        $i2 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        if ($i2 != 16) {
            goto examples.FiniteStack#Pop_2;
        }
    
    examples.FiniteStack#Pop_1:
        $i3 := Union2Int(Read($Heap, r0, examples.FiniteStack#max));
        $i4 := $i3 + 1;
        assume Union2Int(Int2Union($i4)) == $i4;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union($i4));
    
    examples.FiniteStack#Pop_2:
        return;
    
}

procedure examples.FiniteStack#Pop_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.FiniteStack#Pop_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        if ($i0 <= -1) {
            goto examples.FiniteStack#Pop_pre_2;
        }
    
    examples.FiniteStack#Pop_pre_1:
        $z0 := true;
        goto examples.FiniteStack#Pop_pre_3;
    
    examples.FiniteStack#Pop_pre_2:
        $z0 := false;
    
    examples.FiniteStack#Pop_pre_3:
        r := $z0;
        return;
    
}

procedure examples.FiniteStack#Push($this : Ref)
{
    var r0 : Ref;
    var i0 : int;
    var $i1 : int;
    var $r1 : Ref;
    var $i2 : int;
    var $i3 : int;
    var $r2 : Ref;
    var $i4 : int;
    

    examples.FiniteStack#Push_0:
        r0 := $this;
        i0 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        $i1 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        if ($i1 != 5) {
            goto examples.FiniteStack#Push_2;
        }
    
    examples.FiniteStack#Push_1:
        call $r1 := Alloc();
        call java.lang.RuntimeException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.FiniteStack#Push_2:
        $i2 := Union2Int(Read($Heap, r0, examples.FiniteStack#max));
        $i3 := $i2 + 1;
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union($i3));
        if (i0 != 6) {
            goto examples.FiniteStack#Push_4;
        }
    
    examples.FiniteStack#Push_3:
        call $r2 := Alloc();
        call java.lang.RuntimeException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.FiniteStack#Push_4:
        $i4 := i0 + 1;
        assume Union2Int(Int2Union($i4)) == $i4;
        $Heap := Write($Heap, r0, examples.FiniteStack#next, Int2Union($i4));
        return;
    
}

procedure examples.FiniteStack#Push_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $z0 : bool;
    

    examples.FiniteStack#Push_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        $i1 := Union2Int(Read($Heap, r0, examples.FiniteStack#max));
        if ($i0 >= $i1) {
            goto examples.FiniteStack#Push_pre_2;
        }
    
    examples.FiniteStack#Push_pre_1:
        $z0 := true;
        goto examples.FiniteStack#Push_pre_3;
    
    examples.FiniteStack#Push_pre_2:
        $z0 := false;
    
    examples.FiniteStack#Push_pre_3:
        r := $z0;
        return;
    
}

procedure examples.FiniteStack#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $z0 : bool;
    

    examples.FiniteStack#inv_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.FiniteStack#max));
        if ($i0 <= 2) {
            goto examples.FiniteStack#inv_4;
        }
    
    examples.FiniteStack#inv_1:
        $i1 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        if ($i1 < -1) {
            goto examples.FiniteStack#inv_4;
        }
    
    examples.FiniteStack#inv_2:
        $i2 := Union2Int(Read($Heap, r0, examples.FiniteStack#max));
        $i3 := Union2Int(Read($Heap, r0, examples.FiniteStack#next));
        if ($i2 < $i3) {
            goto examples.FiniteStack#inv_4;
        }
    
    examples.FiniteStack#inv_3:
        $z0 := true;
        goto examples.FiniteStack#inv_5;
    
    examples.FiniteStack#inv_4:
        $z0 := false;
    
    examples.FiniteStack#inv_5:
        r := $z0;
        return;
    
}

procedure examples.FiniteStack.FiniteStack_pre() returns (r : bool)
{
    
    

    examples.FiniteStack.FiniteStack_pre_0:
        r := true;
        return;
    
}

procedure examples.FiniteStack.FiniteStack_pre$int(i0 : int) returns (r : bool)
{
    var $z0 : bool;
    

    examples.FiniteStack.FiniteStack_pre$int_0:
        
        if (i0 <= 2) {
            goto examples.FiniteStack.FiniteStack_pre$int_2;
        }
    
    examples.FiniteStack.FiniteStack_pre$int_1:
        $z0 := true;
        goto examples.FiniteStack.FiniteStack_pre$int_3;
    
    examples.FiniteStack.FiniteStack_pre$int_2:
        $z0 := false;
    
    examples.FiniteStack.FiniteStack_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.GenericStack#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.GenericStack#?init?_0:
        r0 := $this;
        call examples.GenericStack#?init?$int(r0, 5);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure examples.GenericStack#?init?$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.GenericStack#?init?$int_0:
        r0 := $this;
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.GenericStack#size, Int2Union(0));
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.GenericStack#capacity, Int2Union(i0));
        call $r1 := Alloc();
        assume $ArrayLength($r1) == i0;
        $Heap := Write($Heap, r0, examples.GenericStack#data, $r1);
        return;
    
}

procedure examples.GenericStack#Pop($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var $r2 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r3 : Ref;
    

    examples.GenericStack#Pop_0:
        r0 := $this;
        $r2 := Read($Heap, r0, examples.GenericStack#data);
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        $i1 := $i0 - 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.GenericStack#size, Int2Union($i1));
        $r3 := $ArrayContents[$r2][$i1];
        r := $r3;
        return;
    
}

procedure examples.GenericStack#Pop_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.GenericStack#Pop_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        if ($i0 <= 0) {
            goto examples.GenericStack#Pop_pre_2;
        }
    
    examples.GenericStack#Pop_pre_1:
        $z0 := true;
        goto examples.GenericStack#Pop_pre_3;
    
    examples.GenericStack#Pop_pre_2:
        $z0 := false;
    
    examples.GenericStack#Pop_pre_3:
        r := $z0;
        return;
    
}

procedure examples.GenericStack#Push$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var $r3 : Ref;
    var $i1 : int;
    

    examples.GenericStack#Push$java.lang.Object_0:
        r0 := $this;
        
        $r3 := Read($Heap, r0, examples.GenericStack#data);
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        $i1 := $i0 + 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.GenericStack#size, Int2Union($i1));
        assert $r3 != null;
        $ArrayContents := $ArrayContents[$r3 := $ArrayContents[$r3][$i0 := r1]];
        return;
    
}

procedure examples.GenericStack#Push_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $z0 : bool;
    

    examples.GenericStack#Push_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        $i1 := Union2Int(Read($Heap, r0, examples.GenericStack#capacity));
        if ($i0 >= $i1) {
            goto examples.GenericStack#Push_pre_2;
        }
    
    examples.GenericStack#Push_pre_1:
        $z0 := true;
        goto examples.GenericStack#Push_pre_3;
    
    examples.GenericStack#Push_pre_2:
        $z0 := false;
    
    examples.GenericStack#Push_pre_3:
        r := $z0;
        return;
    
}

procedure examples.GenericStack#getSize($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $i0 : int;
    

    examples.GenericStack#getSize_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        r := $i0;
        return;
    
}

procedure examples.GenericStack#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $r1 : Ref;
    var $i3 : int;
    var $i4 : int;
    var $r2 : Ref;
    var $z0 : bool;
    

    examples.GenericStack#inv_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        if ($i0 < 0) {
            goto examples.GenericStack#inv_5;
        }
    
    examples.GenericStack#inv_1:
        $i1 := Union2Int(Read($Heap, r0, examples.GenericStack#size));
        $i2 := Union2Int(Read($Heap, r0, examples.GenericStack#capacity));
        if ($i1 > $i2) {
            goto examples.GenericStack#inv_5;
        }
    
    examples.GenericStack#inv_2:
        $r1 := Read($Heap, r0, examples.GenericStack#data);
        $i3 := $ArrayLength($r1);
        $i4 := Union2Int(Read($Heap, r0, examples.GenericStack#capacity));
        if ($i3 != $i4) {
            goto examples.GenericStack#inv_5;
        }
    
    examples.GenericStack#inv_3:
        $r2 := Read($Heap, r0, examples.GenericStack#data);
        if ($r2 == null) {
            goto examples.GenericStack#inv_5;
        }
    
    examples.GenericStack#inv_4:
        $z0 := true;
        goto examples.GenericStack#inv_6;
    
    examples.GenericStack#inv_5:
        $z0 := false;
    
    examples.GenericStack#inv_6:
        r := $z0;
        return;
    
}

procedure examples.GenericStack.GenericStack_pre$int(i0 : int) returns (r : bool)
{
    var $z0 : bool;
    

    examples.GenericStack.GenericStack_pre$int_0:
        
        if (i0 <= 0) {
            goto examples.GenericStack.GenericStack_pre$int_2;
        }
    
    examples.GenericStack.GenericStack_pre$int_1:
        $z0 := true;
        goto examples.GenericStack.GenericStack_pre$int_3;
    
    examples.GenericStack.GenericStack_pre$int_2:
        $z0 := false;
    
    examples.GenericStack.GenericStack_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.List#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.List#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.List#size, Int2Union(0));
        return;
    
}

procedure examples.List#add($this : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    

    examples.List#add_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.List#size));
        $i1 := $i0 + 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.List#size, Int2Union($i1));
        return;
    
}

procedure examples.List#add_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    

    examples.List#add_pre_0:
        r0 := $this;
        r := true;
        return;
    
}

procedure examples.List#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.List#inv_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.List#size));
        if ($i0 < 0) {
            goto examples.List#inv_2;
        }
    
    examples.List#inv_1:
        $z0 := true;
        goto examples.List#inv_3;
    
    examples.List#inv_2:
        $z0 := false;
    
    examples.List#inv_3:
        r := $z0;
        return;
    
}

procedure examples.List#remove($this : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    

    examples.List#remove_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.List#size));
        $i1 := $i0 - 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.List#size, Int2Union($i1));
        return;
    
}

procedure examples.List#remove_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.List#remove_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.List#size));
        if ($i0 <= 0) {
            goto examples.List#remove_pre_2;
        }
    
    examples.List#remove_pre_1:
        $z0 := true;
        goto examples.List#remove_pre_3;
    
    examples.List#remove_pre_2:
        $z0 := false;
    
    examples.List#remove_pre_3:
        r := $z0;
        return;
    
}

procedure examples.List.List_pre() returns (r : bool)
{
    
    

    examples.List.List_pre_0:
        r := true;
        return;
    
}

procedure examples.ListIterator.MyArrayList#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.ListIterator.MyArrayList#?init?_0:
        r0 := $this;
        call examples.ListIterator.MyArrayList#?init?$int(r0, 10);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure examples.ListIterator.MyArrayList#?init?$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#?init?$int_0:
        r0 := $this;
        
        call java.util.AbstractCollection#?init?(r0);
        if ($Exception != null) {
            return;
        }
        if (i0 >= 0) {
            goto examples.ListIterator.MyArrayList#?init?$int_2;
        }
    
    examples.ListIterator.MyArrayList#?init?$int_1:
        call $r1 := Alloc();
        call java.lang.IllegalArgumentException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.ListIterator.MyArrayList#?init?$int_2:
        call $r2 := Alloc();
        assume $ArrayLength($r2) == i0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyArrayList#elementData, $r2);
        return;
    
}

procedure examples.ListIterator.MyArrayList#RangeCheck$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $i1 : int;
    var $r1 : Ref;
    

    examples.ListIterator.MyArrayList#RangeCheck$int_0:
        r0 := $this;
        
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        if (i0 < $i1) {
            goto examples.ListIterator.MyArrayList#RangeCheck$int_2;
        }
    
    examples.ListIterator.MyArrayList#RangeCheck$int_1:
        call $r1 := Alloc();
        call java.lang.IndexOutOfBoundsException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.ListIterator.MyArrayList#RangeCheck$int_2:
        return;
    
}

procedure examples.ListIterator.MyArrayList#add$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref)
{
    var r0 : Ref;
    var $i1 : int;
    var $r2 : Ref;
    var $i2 : int;
    var $i3 : int;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i4 : int;
    var $i5 : int;
    var $i6 : int;
    var $r5 : Ref;
    var $i7 : int;
    var $i8 : int;
    

    examples.ListIterator.MyArrayList#add$int$java.lang.Object_0:
        r0 := $this;
        
        
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        if (i0 > $i1) {
            goto examples.ListIterator.MyArrayList#add$int$java.lang.Object_2;
        }
    
    examples.ListIterator.MyArrayList#add$int$java.lang.Object_1:
        if (i0 >= 0) {
            goto examples.ListIterator.MyArrayList#add$int$java.lang.Object_3;
        }
    
    examples.ListIterator.MyArrayList#add$int$java.lang.Object_2:
        call $r2 := Alloc();
        call java.lang.IndexOutOfBoundsException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.ListIterator.MyArrayList#add$int$java.lang.Object_3:
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i3 := $i2 + 1;
        call examples.ListIterator.MyArrayList#ensureCapacity$int(r0, $i3);
        if ($Exception != null) {
            return;
        }
        $r3 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $r4 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i4 := i0 + 1;
        $i5 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i6 := $i5 - i0;
        call java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int($r3, i0, $r4, $i4, $i6);
        if ($Exception != null) {
            return;
        }
        $r5 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        assert $r5 != null;
        $ArrayContents := $ArrayContents[$r5 := $ArrayContents[$r5][i0 := r1]];
        $i7 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i8 := $i7 + 1;
        assume Union2Int(Int2Union($i8)) == $i8;
        $Heap := Write($Heap, r0, examples.ListIterator.MyArrayList#size, Int2Union($i8));
        return;
    
}

procedure examples.ListIterator.MyArrayList#add$java.lang.Object($this : Ref, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $r2 : Ref;
    var $i3 : int;
    

    examples.ListIterator.MyArrayList#add$java.lang.Object_0:
        r0 := $this;
        
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i2 := $i1 + 1;
        call examples.ListIterator.MyArrayList#ensureCapacity$int(r0, $i2);
        if ($Exception != null) {
            return;
        }
        $r2 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i3 := $i0 + 1;
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r0, examples.ListIterator.MyArrayList#size, Int2Union($i3));
        assert $r2 != null;
        $ArrayContents := $ArrayContents[$r2 := $ArrayContents[$r2][$i0 := r1]];
        r := true;
        return;
    
}

procedure examples.ListIterator.MyArrayList#addAll$int$java.util.Collection($this : Ref, i0 : int, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#addAll$int$java.util.Collection_0:
        r0 := $this;
        
        
        call $r2 := Alloc();
        call java.lang.IllegalStateException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#ensureCapacity$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var i1 : int;
    var r1 : Ref;
    var i2 : int;
    var $r2 : Ref;
    var $i3 : int;
    var $i4 : int;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i5 : int;
    

    examples.ListIterator.MyArrayList#ensureCapacity$int_0:
        r0 := $this;
        
        $r2 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        i1 := $ArrayLength($r2);
        if (i0 <= i1) {
            goto examples.ListIterator.MyArrayList#ensureCapacity$int_4;
        }
    
    examples.ListIterator.MyArrayList#ensureCapacity$int_1:
        r1 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i3 := i1 * 3;
        call $i4 := division($i3, 2);
        if ($Exception != null) {
            return;
        }
        i2 := $i4 + 1;
        if (i2 >= i0) {
            goto examples.ListIterator.MyArrayList#ensureCapacity$int_3;
        }
    
    examples.ListIterator.MyArrayList#ensureCapacity$int_2:
        i2 := i0;
    
    examples.ListIterator.MyArrayList#ensureCapacity$int_3:
        call $r3 := Alloc();
        assume $ArrayLength($r3) == i2;
        $Heap := Write($Heap, r0, examples.ListIterator.MyArrayList#elementData, $r3);
        $r4 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i5 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        call java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int(r1, 0, $r4, 0, $i5);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyArrayList#ensureCapacity$int_4:
        return;
    
}

procedure examples.ListIterator.MyArrayList#get$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#get$int_0:
        r0 := $this;
        
        call examples.ListIterator.MyArrayList#RangeCheck$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $r1 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $r2 := $ArrayContents[$r1][i0];
        r := $r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#indexOf$java.lang.Object($this : Ref, r1 : Ref) returns (r : int)
{
    var r0 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#indexOf$java.lang.Object_0:
        r0 := $this;
        
        call $r2 := Alloc();
        call java.lang.IllegalStateException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#isEmpty($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyArrayList#isEmpty_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        if ($i0 != 0) {
            goto examples.ListIterator.MyArrayList#isEmpty_2;
        }
    
    examples.ListIterator.MyArrayList#isEmpty_1:
        $z0 := true;
        goto examples.ListIterator.MyArrayList#isEmpty_3;
    
    examples.ListIterator.MyArrayList#isEmpty_2:
        $z0 := false;
    
    examples.ListIterator.MyArrayList#isEmpty_3:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyArrayList#iterator($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.ListIterator.MyArrayList#iterator_0:
        r0 := $this;
        call $r1 := Alloc();
        call examples.ListIterator.MyListItr#?init?$examples.ListIterator.MyArrayList$int($r1, r0, 0);
        if ($Exception != null) {
            return;
        }
        r := $r1;
        return;
    
}

procedure examples.ListIterator.MyArrayList#lastIndexOf$java.lang.Object($this : Ref, r1 : Ref) returns (r : int)
{
    var r0 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#lastIndexOf$java.lang.Object_0:
        r0 := $this;
        
        call $r2 := Alloc();
        call java.lang.IllegalStateException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#listIterator($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.ListIterator.MyArrayList#listIterator_0:
        r0 := $this;
        call $r1 := examples.ListIterator.MyArrayList#listIterator$int(r0, 0);
        if ($Exception != null) {
            return;
        }
        r := $r1;
        return;
    
}

procedure examples.ListIterator.MyArrayList#listIterator$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $i1 : int;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.ListIterator.MyArrayList#listIterator$int_0:
        r0 := $this;
        
        if (i0 < 0) {
            goto examples.ListIterator.MyArrayList#listIterator$int_2;
        }
    
    examples.ListIterator.MyArrayList#listIterator$int_1:
        call $i1 := examples.ListIterator.MyArrayList#size(r0);
        if ($Exception != null) {
            return;
        }
        if (i0 < $i1) {
            goto examples.ListIterator.MyArrayList#listIterator$int_3;
        }
    
    examples.ListIterator.MyArrayList#listIterator$int_2:
        call $r1 := Alloc();
        call java.lang.IndexOutOfBoundsException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.ListIterator.MyArrayList#listIterator$int_3:
        call $r2 := Alloc();
        call examples.ListIterator.MyListItr#?init?$examples.ListIterator.MyArrayList$int($r2, r0, i0);
        if ($Exception != null) {
            return;
        }
        r := $r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#remove$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var i1 : int;
    var $r2 : Ref;
    var $i2 : int;
    var $i3 : int;
    var $r3 : Ref;
    var $i4 : int;
    var $r4 : Ref;
    var $r5 : Ref;
    var $i5 : int;
    var $i6 : int;
    

    examples.ListIterator.MyArrayList#remove$int_0:
        r0 := $this;
        
        call examples.ListIterator.MyArrayList#RangeCheck$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $r2 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        r1 := $ArrayContents[$r2][i0];
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i3 := $i2 - i0;
        i1 := $i3 - 1;
        if (i1 <= 0) {
            goto examples.ListIterator.MyArrayList#remove$int_2;
        }
    
    examples.ListIterator.MyArrayList#remove$int_1:
        $r3 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i4 := i0 + 1;
        $r4 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        call java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int($r3, $i4, $r4, i0, i1);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyArrayList#remove$int_2:
        $r5 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        $i5 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        $i6 := $i5 - 1;
        assume Union2Int(Int2Union($i6)) == $i6;
        $Heap := Write($Heap, r0, examples.ListIterator.MyArrayList#size, Int2Union($i6));
        assert $r5 != null;
        $ArrayContents := $ArrayContents[$r5 := $ArrayContents[$r5][$i6 := null]];
        r := r1;
        return;
    
}

procedure examples.ListIterator.MyArrayList#set$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var r2 : Ref;
    var $r3 : Ref;
    var $r4 : Ref;
    

    examples.ListIterator.MyArrayList#set$int$java.lang.Object_0:
        r0 := $this;
        
        
        call examples.ListIterator.MyArrayList#RangeCheck$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $r3 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        r2 := $ArrayContents[$r3][i0];
        $r4 := Read($Heap, r0, examples.ListIterator.MyArrayList#elementData);
        assert $r4 != null;
        $ArrayContents := $ArrayContents[$r4 := $ArrayContents[$r4][i0 := r1]];
        r := r2;
        return;
    
}

procedure examples.ListIterator.MyArrayList#size($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $i0 : int;
    

    examples.ListIterator.MyArrayList#size_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyArrayList#size));
        r := $i0;
        return;
    
}

procedure examples.ListIterator.MyArrayList#subList$int$int($this : Ref, i0 : int, i1 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.ListIterator.MyArrayList#subList$int$int_0:
        r0 := $this;
        
        
        call $r1 := Alloc();
        call java.lang.IllegalStateException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
}

procedure examples.ListIterator.MyListItr#?init?$examples.ListIterator.MyArrayList$int($this : Ref, r1 : Ref, i0 : int)
{
    var r0 : Ref;
    

    examples.ListIterator.MyListItr#?init?$examples.ListIterator.MyArrayList$int_0:
        r0 := $this;
        
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union(0));
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#lastRet, Int2Union(-1));
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#list, r1);
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union(i0));
        return;
    
}

procedure examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int($this : Ref, r1 : Ref, i0 : int) returns (r : bool)
{
    var r0 : Ref;
    var $r2 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $r3 : Ref;
    var $i4 : int;
    var $r4 : Ref;
    var $i5 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_0:
        r0 := $this;
        
        
        if (r1 == null) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_1:
        $r2 := Read($Heap, r1, examples.ListIterator.MyArrayList#elementData);
        if ($r2 == null) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_2:
        if (0 > i0) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_3:
        $i1 := Union2Int(Read($Heap, r1, examples.ListIterator.MyArrayList#size));
        if (i0 > $i1) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_4:
        $i2 := Union2Int(Read($Heap, r1, examples.ListIterator.MyArrayList#size));
        if (0 > $i2) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_5:
        $i3 := Union2Int(Read($Heap, r1, examples.ListIterator.MyArrayList#size));
        $r3 := Read($Heap, r1, examples.ListIterator.MyArrayList#elementData);
        $i4 := $ArrayLength($r3);
        if ($i3 > $i4) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_6:
        $r4 := Read($Heap, r1, examples.ListIterator.MyArrayList#elementData);
        $i5 := $ArrayLength($r4);
        if (10 > $i5) {
            goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8;
        }
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_7:
        $z0 := true;
        goto examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_9;
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_8:
        $z0 := false;
    
    examples.ListIterator.MyListItr#MyListItr_pre$examples.ListIterator.MyArrayList$int_9:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#add$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var r3 : Ref;
    var $r4 : Ref;
    var $i1 : int;
    var $r5 : Ref;
    var $r6 : Ref;
    

    examples.ListIterator.MyListItr#add$java.lang.Object_0:
        r0 := $this;
        
    
    examples.ListIterator.MyListItr#add$java.lang.Object_1:
        $r4 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
    
    examples.ListIterator.MyListItr#add$java.lang.Object_2:
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#add$java.lang.Object_3:
        $i1 := $i0 + 1;
    
    examples.ListIterator.MyListItr#add$java.lang.Object_4:
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union($i1));
    
    examples.ListIterator.MyListItr#add$java.lang.Object_5:
        call examples.ListIterator.MyArrayList#add$int$java.lang.Object($r4, $i0, r1);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyListItr#add$java.lang.Object_6:
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#lastRet, Int2Union(-1));
    
    examples.ListIterator.MyListItr#add$java.lang.Object_7:
        goto examples.ListIterator.MyListItr#add$java.lang.Object_9;
    
    examples.ListIterator.MyListItr#add$java.lang.Object_8:
        $r5 := $Exception;
        r3 := $r5;
        call $r6 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r6);
        if ($Exception != null) {
            return;
        }
        $Exception := $r6;
        return;
    
    examples.ListIterator.MyListItr#add$java.lang.Object_9:
        return;
    
}

procedure examples.ListIterator.MyListItr#add_pre$java.lang.Object($this : Ref, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r2 : Ref;
    var $i2 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#add_pre$java.lang.Object_0:
        r0 := $this;
        
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        if (0 > $i0) {
            goto examples.ListIterator.MyListItr#add_pre$java.lang.Object_3;
        }
    
    examples.ListIterator.MyListItr#add_pre$java.lang.Object_1:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $r2 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i2 := Union2Int(Read($Heap, $r2, examples.ListIterator.MyArrayList#size));
        if ($i1 > $i2) {
            goto examples.ListIterator.MyListItr#add_pre$java.lang.Object_3;
        }
    
    examples.ListIterator.MyListItr#add_pre$java.lang.Object_2:
        $z0 := true;
        goto examples.ListIterator.MyListItr#add_pre$java.lang.Object_4;
    
    examples.ListIterator.MyListItr#add_pre$java.lang.Object_3:
        $z0 := false;
    
    examples.ListIterator.MyListItr#add_pre$java.lang.Object_4:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#hasNext($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $r1 : Ref;
    var $i1 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#hasNext_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $r1 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        call $i1 := examples.ListIterator.MyArrayList#size($r1);
        if ($Exception != null) {
            return;
        }
        if ($i0 == $i1) {
            goto examples.ListIterator.MyListItr#hasNext_2;
        }
    
    examples.ListIterator.MyListItr#hasNext_1:
        $z0 := true;
        goto examples.ListIterator.MyListItr#hasNext_3;
    
    examples.ListIterator.MyListItr#hasNext_2:
        $z0 := false;
    
    examples.ListIterator.MyListItr#hasNext_3:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#hasPrevious($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#hasPrevious_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        if ($i0 == 0) {
            goto examples.ListIterator.MyListItr#hasPrevious_2;
        }
    
    examples.ListIterator.MyListItr#hasPrevious_1:
        $z0 := true;
        goto examples.ListIterator.MyListItr#hasPrevious_3;
    
    examples.ListIterator.MyListItr#hasPrevious_2:
        $z0 := false;
    
    examples.ListIterator.MyListItr#hasPrevious_3:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    var $r3 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r4 : Ref;
    var $i2 : int;
    var $r5 : Ref;
    var $i3 : int;
    var $r6 : Ref;
    var $r7 : Ref;
    var $i4 : int;
    var $i5 : int;
    var $i6 : int;
    var $i7 : int;
    var $i8 : int;
    var $i9 : int;
    var $i10 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#inv_0:
        r0 := $this;
        $r1 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        if ($r1 == null) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_1:
        $r2 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $r3 := Read($Heap, $r2, examples.ListIterator.MyArrayList#elementData);
        if ($r3 == null) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_2:
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        if ($i0 < 0) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_3:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $r4 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i2 := Union2Int(Read($Heap, $r4, examples.ListIterator.MyArrayList#size));
        if ($i1 > $i2) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_4:
        $r5 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i3 := Union2Int(Read($Heap, $r5, examples.ListIterator.MyArrayList#size));
        $r6 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $r7 := Read($Heap, $r6, examples.ListIterator.MyArrayList#elementData);
        $i4 := $ArrayLength($r7);
        if ($i3 > $i4) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_5:
        $i5 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if ($i5 == -1) {
            goto examples.ListIterator.MyListItr#inv_8;
        }
    
    examples.ListIterator.MyListItr#inv_6:
        $i6 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $i7 := $i6 - 1;
        $i8 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if ($i7 > $i8) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_7:
        $i9 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        $i10 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        if ($i9 > $i10) {
            goto examples.ListIterator.MyListItr#inv_9;
        }
    
    examples.ListIterator.MyListItr#inv_8:
        $z0 := true;
        goto examples.ListIterator.MyListItr#inv_10;
    
    examples.ListIterator.MyListItr#inv_9:
        $z0 := false;
    
    examples.ListIterator.MyListItr#inv_10:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#next($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var $i0 : int;
    var $r2 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $r5 : Ref;
    var r6 : Ref;
    var $r7 : Ref;
    

    examples.ListIterator.MyListItr#next_0:
        r0 := $this;
    
    examples.ListIterator.MyListItr#next_1:
        $r2 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
    
    examples.ListIterator.MyListItr#next_2:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#next_3:
        call r1 := examples.ListIterator.MyArrayList#get$int($r2, $i1);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyListItr#next_4:
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#next_5:
        $i2 := $i0 + 1;
    
    examples.ListIterator.MyListItr#next_6:
        assume Union2Int(Int2Union($i2)) == $i2;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union($i2));
    
    examples.ListIterator.MyListItr#next_7:
        assume Union2Int(Int2Union($i0)) == $i0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#lastRet, Int2Union($i0));
    
    examples.ListIterator.MyListItr#next_8:
        r := r1;
        return;
    
    examples.ListIterator.MyListItr#next_9:
        $r5 := $Exception;
        r6 := $r5;
        call $r7 := Alloc();
        call java.util.NoSuchElementException#?init?($r7);
        if ($Exception != null) {
            return;
        }
        $Exception := $r7;
        return;
    
}

procedure examples.ListIterator.MyListItr#nextIndex($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.ListIterator.MyListItr#nextIndex_0:
        r0 := $this;
        call $r1 := Alloc();
        call java.lang.IllegalStateException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
}

procedure examples.ListIterator.MyListItr#next_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r1 : Ref;
    var $i2 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#next_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        if (0 > $i0) {
            goto examples.ListIterator.MyListItr#next_pre_3;
        }
    
    examples.ListIterator.MyListItr#next_pre_1:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $r1 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i2 := Union2Int(Read($Heap, $r1, examples.ListIterator.MyArrayList#size));
        if ($i1 >= $i2) {
            goto examples.ListIterator.MyListItr#next_pre_3;
        }
    
    examples.ListIterator.MyListItr#next_pre_2:
        $z0 := true;
        goto examples.ListIterator.MyListItr#next_pre_4;
    
    examples.ListIterator.MyListItr#next_pre_3:
        $z0 := false;
    
    examples.ListIterator.MyListItr#next_pre_4:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#previous($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var i0 : int;
    var r1 : Ref;
    var $i2 : int;
    var $r2 : Ref;
    var $r4 : Ref;
    var r5 : Ref;
    var $r6 : Ref;
    

    examples.ListIterator.MyListItr#previous_0:
        r0 := $this;
    
    examples.ListIterator.MyListItr#previous_1:
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#previous_2:
        i0 := $i2 - 1;
    
    examples.ListIterator.MyListItr#previous_3:
        $r2 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
    
    examples.ListIterator.MyListItr#previous_4:
        call r1 := examples.ListIterator.MyArrayList#get$int($r2, i0);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyListItr#previous_5:
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union(i0));
    
    examples.ListIterator.MyListItr#previous_6:
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#lastRet, Int2Union(i0));
    
    examples.ListIterator.MyListItr#previous_7:
        r := r1;
        return;
    
    examples.ListIterator.MyListItr#previous_8:
        $r4 := $Exception;
        r5 := $r4;
        call $r6 := Alloc();
        call java.util.NoSuchElementException#?init?($r6);
        if ($Exception != null) {
            return;
        }
        $Exception := $r6;
        return;
    
}

procedure examples.ListIterator.MyListItr#previousIndex($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.ListIterator.MyListItr#previousIndex_0:
        r0 := $this;
        call $r1 := Alloc();
        call java.lang.IllegalStateException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
}

procedure examples.ListIterator.MyListItr#previous_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $r1 : Ref;
    var $i4 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#previous_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $i1 := $i0 - 1;
        if (0 > $i1) {
            goto examples.ListIterator.MyListItr#previous_pre_3;
        }
    
    examples.ListIterator.MyListItr#previous_pre_1:
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
        $i3 := $i2 - 1;
        $r1 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i4 := Union2Int(Read($Heap, $r1, examples.ListIterator.MyArrayList#size));
        if ($i3 >= $i4) {
            goto examples.ListIterator.MyListItr#previous_pre_3;
        }
    
    examples.ListIterator.MyListItr#previous_pre_2:
        $z0 := true;
        goto examples.ListIterator.MyListItr#previous_pre_4;
    
    examples.ListIterator.MyListItr#previous_pre_3:
        $z0 := false;
    
    examples.ListIterator.MyListItr#previous_pre_4:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#remove($this : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var $i0 : int;
    var $r2 : Ref;
    var $r3 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $i4 : int;
    var $i5 : int;
    var $r6 : Ref;
    var $r7 : Ref;
    var $ret$1415439780 : Ref;

    examples.ListIterator.MyListItr#remove_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if ($i0 != -1) {
            goto examples.ListIterator.MyListItr#remove_2;
        }
    
    examples.ListIterator.MyListItr#remove_1:
        call $r2 := Alloc();
        call java.lang.IllegalStateException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.ListIterator.MyListItr#remove_2:
        $r3 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
    
    examples.ListIterator.MyListItr#remove_3:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
    
    examples.ListIterator.MyListItr#remove_4:
        call $ret$1415439780 := examples.ListIterator.MyArrayList#remove$int($r3, $i1);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyListItr#remove_5:
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
    
    examples.ListIterator.MyListItr#remove_6:
        $i3 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#remove_7:
        if ($i2 <= $i3) {
            goto examples.ListIterator.MyListItr#remove_11;
        }
    
    examples.ListIterator.MyListItr#remove_8:
        $i4 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#cursor));
    
    examples.ListIterator.MyListItr#remove_9:
        $i5 := $i4 - 1;
    
    examples.ListIterator.MyListItr#remove_10:
        assume Union2Int(Int2Union($i5)) == $i5;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#cursor, Int2Union($i5));
    
    examples.ListIterator.MyListItr#remove_11:
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.ListIterator.MyListItr#lastRet, Int2Union(-1));
    
    examples.ListIterator.MyListItr#remove_12:
        goto examples.ListIterator.MyListItr#remove_14;
    
    examples.ListIterator.MyListItr#remove_13:
        $r6 := $Exception;
        r1 := $r6;
        call $r7 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r7);
        if ($Exception != null) {
            return;
        }
        $Exception := $r7;
        return;
    
    examples.ListIterator.MyListItr#remove_14:
        return;
    
}

procedure examples.ListIterator.MyListItr#remove_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $i2 : int;
    var $r1 : Ref;
    var $i3 : int;
    var $z0 : bool;
    

    examples.ListIterator.MyListItr#remove_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if ($i0 == -1) {
            goto examples.ListIterator.MyListItr#remove_pre_4;
        }
    
    examples.ListIterator.MyListItr#remove_pre_1:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if (0 > $i1) {
            goto examples.ListIterator.MyListItr#remove_pre_4;
        }
    
    examples.ListIterator.MyListItr#remove_pre_2:
        $i2 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        $r1 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
        $i3 := Union2Int(Read($Heap, $r1, examples.ListIterator.MyArrayList#size));
        if ($i2 >= $i3) {
            goto examples.ListIterator.MyListItr#remove_pre_4;
        }
    
    examples.ListIterator.MyListItr#remove_pre_3:
        $z0 := true;
        goto examples.ListIterator.MyListItr#remove_pre_5;
    
    examples.ListIterator.MyListItr#remove_pre_4:
        $z0 := false;
    
    examples.ListIterator.MyListItr#remove_pre_5:
        r := $z0;
        return;
    
}

procedure examples.ListIterator.MyListItr#set$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var r2 : Ref;
    var $i0 : int;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i1 : int;
    var $r6 : Ref;
    var $r7 : Ref;
    var $ret$636959006 : Ref;

    examples.ListIterator.MyListItr#set$java.lang.Object_0:
        r0 := $this;
        
        $i0 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
        if ($i0 != -1) {
            goto examples.ListIterator.MyListItr#set$java.lang.Object_2;
        }
    
    examples.ListIterator.MyListItr#set$java.lang.Object_1:
        call $r3 := Alloc();
        call java.lang.IllegalStateException#?init?($r3);
        if ($Exception != null) {
            return;
        }
        $Exception := $r3;
        return;
    
    examples.ListIterator.MyListItr#set$java.lang.Object_2:
        $r4 := Read($Heap, r0, examples.ListIterator.MyListItr#list);
    
    examples.ListIterator.MyListItr#set$java.lang.Object_3:
        $i1 := Union2Int(Read($Heap, r0, examples.ListIterator.MyListItr#lastRet));
    
    examples.ListIterator.MyListItr#set$java.lang.Object_4:
        call $ret$636959006 := examples.ListIterator.MyArrayList#set$int$java.lang.Object($r4, $i1, r1);
        if ($Exception != null) {
            return;
        }
    
    examples.ListIterator.MyListItr#set$java.lang.Object_5:
        goto examples.ListIterator.MyListItr#set$java.lang.Object_7;
    
    examples.ListIterator.MyListItr#set$java.lang.Object_6:
        $r6 := $Exception;
        r2 := $r6;
        call $r7 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r7);
        if ($Exception != null) {
            return;
        }
        $Exception := $r7;
        return;
    
    examples.ListIterator.MyListItr#set$java.lang.Object_7:
        return;
    
}

procedure examples.PreconditionOnParametersTest#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.PreconditionOnParametersTest#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure examples.PreconditionOnParametersTest#alwaysEnabledMethod$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    

    examples.PreconditionOnParametersTest#alwaysEnabledMethod$java.lang.Object_0:
        r0 := $this;
        
        return;
    
}

procedure examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object($this : Ref, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_0:
        r0 := $this;
        
        if (r1 == null) {
            goto examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_2;
        }
    
    examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_1:
        $z0 := true;
        goto examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_3;
    
    examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_2:
        $z0 := false;
    
    examples.PreconditionOnParametersTest#alwaysEnabledMethod_pre$java.lang.Object_3:
        r := $z0;
        return;
    
}

procedure examples.PreconditionOnParametersTest#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    

    examples.PreconditionOnParametersTest#inv_0:
        r0 := $this;
        r := true;
        return;
    
}

procedure examples.PreconditionOnParametersTest.PreconditionOnParametersTest_pre() returns (r : bool)
{
    
    

    examples.PreconditionOnParametersTest.PreconditionOnParametersTest_pre_0:
        r := true;
        return;
    
}

procedure examples.Switch#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.Switch#?init?_0:
        r0 := $this;
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.Switch#isOn, Bool2Union(false));
        return;
    
}

procedure examples.Switch#?init?$bool($this : Ref, z0 : bool)
{
    var r0 : Ref;
    

    examples.Switch#?init?$bool_0:
        r0 := $this;
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Bool(Bool2Union(z0)) == z0;
        $Heap := Write($Heap, r0, examples.Switch#isOn, Bool2Union(z0));
        return;
    
}

procedure examples.Switch#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    

    examples.Switch#inv_0:
        r0 := $this;
        r := true;
        return;
    
}

procedure examples.Switch#off($this : Ref)
{
    var r0 : Ref;
    

    examples.Switch#off_0:
        r0 := $this;
        assume Union2Bool(Bool2Union(false)) == false;
        $Heap := Write($Heap, r0, examples.Switch#isOn, Bool2Union(false));
        return;
    
}

procedure examples.Switch#off_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.Switch#off_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.Switch#isOn));
        r := $z0;
        return;
    
}

procedure examples.Switch#on($this : Ref)
{
    var r0 : Ref;
    

    examples.Switch#on_0:
        r0 := $this;
        assume Union2Bool(Bool2Union(true)) == true;
        $Heap := Write($Heap, r0, examples.Switch#isOn, Bool2Union(true));
        return;
    
}

procedure examples.Switch#on_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    var $z1 : bool;
    

    examples.Switch#on_pre_0:
        r0 := $this;
        $z0 := Union2Bool(Read($Heap, r0, examples.Switch#isOn));
        if ($z0 != false) {
            goto examples.Switch#on_pre_2;
        }
    
    examples.Switch#on_pre_1:
        $z1 := true;
        goto examples.Switch#on_pre_3;
    
    examples.Switch#on_pre_2:
        $z1 := false;
    
    examples.Switch#on_pre_3:
        r := $z1;
        return;
    
}

procedure examples.Switch.Switch_pre() returns (r : bool)
{
    
    

    examples.Switch.Switch_pre_0:
        r := true;
        return;
    
}

procedure examples.Switch.Switch_pre$bool(z0 : bool) returns (r : bool)
{
    
    

    examples.Switch.Switch_pre$bool_0:
        
        r := true;
        return;
    
}

procedure examples.arrayList.ArrayList#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.arrayList.ArrayList#?init?_0:
        r0 := $this;
        call examples.arrayList.ArrayList#?init?$int(r0, 10);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure examples.arrayList.ArrayList#?init?$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    var $r3 : Ref;
    var $r4 : Ref;
    var $r5 : Ref;
    var $r6 : Ref;
    

    examples.arrayList.ArrayList#?init?$int_0:
        r0 := $this;
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#modCount, Int2Union(0));
        if (i0 >= 0) {
            goto examples.arrayList.ArrayList#?init?$int_2;
        }
    
    examples.arrayList.ArrayList#?init?$int_1:
        call $r2 := Alloc();
        call $r1 := Alloc();
        call java.lang.StringBuilder#?init?($r1);
        if ($Exception != null) {
            return;
        }
        call $r3 := java.lang.StringBuilder#append$java.lang.String($r1, stringConstant_396102794_Illegal_Capacity__);
        if ($Exception != null) {
            return;
        }
        call $r4 := java.lang.StringBuilder#append$int($r3, i0);
        if ($Exception != null) {
            return;
        }
        call $r5 := java.lang.StringBuilder#toString($r4);
        if ($Exception != null) {
            return;
        }
        call java.lang.IllegalArgumentException#?init?$java.lang.String($r2, $r5);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.arrayList.ArrayList#?init?$int_2:
        call $r6 := Alloc();
        assume $ArrayLength($r6) == i0;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#elementData, $r6);
        return;
    
}

procedure examples.arrayList.ArrayList#add$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref)
{
    var r0 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $r2 : Ref;
    var $r3 : Ref;
    var $i3 : int;
    var $i4 : int;
    var $i5 : int;
    var $r4 : Ref;
    var $i6 : int;
    var $i7 : int;
    

    examples.arrayList.ArrayList#add$int$java.lang.Object_0:
        r0 := $this;
        
        
        call examples.arrayList.ArrayList#rangeCheckForAdd$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $i2 := $i1 + 1;
        call examples.arrayList.ArrayList#ensureCapacity$int(r0, $i2);
        if ($Exception != null) {
            return;
        }
        $r2 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $r3 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i3 := i0 + 1;
        $i4 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $i5 := $i4 - i0;
        call java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int($r2, i0, $r3, $i3, $i5);
        if ($Exception != null) {
            return;
        }
        $r4 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        assert $r4 != null;
        $ArrayContents := $ArrayContents[$r4 := $ArrayContents[$r4][i0 := r1]];
        $i6 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $i7 := $i6 + 1;
        assume Union2Int(Int2Union($i7)) == $i7;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#size, Int2Union($i7));
        return;
    
}

procedure examples.arrayList.ArrayList#add_pre$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ArrayList#add_pre$int$java.lang.Object_0:
        r0 := $this;
        
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList#add_pre$int$java.lang.Object_2;
        }
    
    examples.arrayList.ArrayList#add_pre$int$java.lang.Object_1:
        $z0 := true;
        goto examples.arrayList.ArrayList#add_pre$int$java.lang.Object_3;
    
    examples.arrayList.ArrayList#add_pre$int$java.lang.Object_2:
        $z0 := false;
    
    examples.arrayList.ArrayList#add_pre$int$java.lang.Object_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ArrayList#elementData$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.arrayList.ArrayList#elementData$int_0:
        r0 := $this;
        
        $r1 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $r2 := $ArrayContents[$r1][i0];
        r := $r2;
        return;
    
}

procedure examples.arrayList.ArrayList#ensureCapacity$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var i1 : int;
    var r2 : Ref;
    var i2 : int;
    var $i3 : int;
    var $i4 : int;
    var $r3 : Ref;
    var $i5 : int;
    var $i6 : int;
    var $r4 : Ref;
    var $r5 : Ref;
    

    examples.arrayList.ArrayList#ensureCapacity$int_0:
        r0 := $this;
        
        $i3 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#modCount));
        $i4 := $i3 + 1;
        assume Union2Int(Int2Union($i4)) == $i4;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#modCount, Int2Union($i4));
        $r3 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        i1 := $ArrayLength($r3);
        if (i0 <= i1) {
            goto examples.arrayList.ArrayList#ensureCapacity$int_4;
        }
    
    examples.arrayList.ArrayList#ensureCapacity$int_1:
        r2 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i5 := i1 * 3;
        call $i6 := division($i5, 2);
        if ($Exception != null) {
            return;
        }
        i2 := $i6 + 1;
        if (i2 >= i0) {
            goto examples.arrayList.ArrayList#ensureCapacity$int_3;
        }
    
    examples.arrayList.ArrayList#ensureCapacity$int_2:
        i2 := i0;
    
    examples.arrayList.ArrayList#ensureCapacity$int_3:
        $r4 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        call $r5 := java.util.Arrays.copyOf$Ref$int($r4, i2);
        if ($Exception != null) {
            return;
        }
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#elementData, $r5);
    
    examples.arrayList.ArrayList#ensureCapacity$int_4:
        return;
    
}

procedure examples.arrayList.ArrayList#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $i0 : int;
    var $r2 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $r3 : Ref;
    var $i3 : int;
    var $i4 : int;
    var $z0 : bool;
    

    examples.arrayList.ArrayList#inv_0:
        r0 := $this;
        $r1 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        if ($r1 == null) {
            goto examples.arrayList.ArrayList#inv_6;
        }
    
    examples.arrayList.ArrayList#inv_1:
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        if ($i0 < 0) {
            goto examples.arrayList.ArrayList#inv_6;
        }
    
    examples.arrayList.ArrayList#inv_2:
        $r2 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i1 := $ArrayLength($r2);
        if ($i1 < 0) {
            goto examples.arrayList.ArrayList#inv_6;
        }
    
    examples.arrayList.ArrayList#inv_3:
        $i2 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $r3 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i3 := $ArrayLength($r3);
        if ($i2 > $i3) {
            goto examples.arrayList.ArrayList#inv_6;
        }
    
    examples.arrayList.ArrayList#inv_4:
        $i4 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#modCount));
        if ($i4 < 0) {
            goto examples.arrayList.ArrayList#inv_6;
        }
    
    examples.arrayList.ArrayList#inv_5:
        $z0 := true;
        goto examples.arrayList.ArrayList#inv_7;
    
    examples.arrayList.ArrayList#inv_6:
        $z0 := false;
    
    examples.arrayList.ArrayList#inv_7:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ArrayList#listIterator($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    

    examples.arrayList.ArrayList#listIterator_0:
        r0 := $this;
        call $r1 := Alloc();
        call examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int($r1, r0, 0);
        if ($Exception != null) {
            return;
        }
        r := $r1;
        return;
    
}

procedure examples.arrayList.ArrayList#listIterator$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $i1 : int;
    var $r2 : Ref;
    var $r3 : Ref;
    var $r4 : Ref;
    var $r5 : Ref;
    var $r6 : Ref;
    

    examples.arrayList.ArrayList#listIterator$int_0:
        r0 := $this;
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList#listIterator$int_2;
        }
    
    examples.arrayList.ArrayList#listIterator$int_1:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        if (i0 <= $i1) {
            goto examples.arrayList.ArrayList#listIterator$int_3;
        }
    
    examples.arrayList.ArrayList#listIterator$int_2:
        call $r2 := Alloc();
        call $r1 := Alloc();
        call java.lang.StringBuilder#?init?($r1);
        if ($Exception != null) {
            return;
        }
        call $r3 := java.lang.StringBuilder#append$java.lang.String($r1, stringConstant_687023272_Index__);
        if ($Exception != null) {
            return;
        }
        call $r4 := java.lang.StringBuilder#append$int($r3, i0);
        if ($Exception != null) {
            return;
        }
        call $r5 := java.lang.StringBuilder#toString($r4);
        if ($Exception != null) {
            return;
        }
        call java.lang.IndexOutOfBoundsException#?init?$java.lang.String($r2, $r5);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.arrayList.ArrayList#listIterator$int_3:
        call $r6 := Alloc();
        call examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int($r6, r0, i0);
        if ($Exception != null) {
            return;
        }
        r := $r6;
        return;
    
}

procedure examples.arrayList.ArrayList#listiterator_pre$int($this : Ref, i0 : int) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ArrayList#listiterator_pre$int_0:
        r0 := $this;
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList#listiterator_pre$int_2;
        }
    
    examples.arrayList.ArrayList#listiterator_pre$int_1:
        $z0 := true;
        goto examples.arrayList.ArrayList#listiterator_pre$int_3;
    
    examples.arrayList.ArrayList#listiterator_pre$int_2:
        $z0 := false;
    
    examples.arrayList.ArrayList#listiterator_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ArrayList#outOfBoundsMsg$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i1 : int;
    var $r5 : Ref;
    var $r6 : Ref;
    

    examples.arrayList.ArrayList#outOfBoundsMsg$int_0:
        r0 := $this;
        
        call $r1 := Alloc();
        call java.lang.StringBuilder#?init?($r1);
        if ($Exception != null) {
            return;
        }
        call $r2 := java.lang.StringBuilder#append$java.lang.String($r1, stringConstant_687023272_Index__);
        if ($Exception != null) {
            return;
        }
        call $r3 := java.lang.StringBuilder#append$int($r2, i0);
        if ($Exception != null) {
            return;
        }
        call $r4 := java.lang.StringBuilder#append$java.lang.String($r3, stringConstant_186512763___Size__);
        if ($Exception != null) {
            return;
        }
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        call $r5 := java.lang.StringBuilder#append$int($r4, $i1);
        if ($Exception != null) {
            return;
        }
        call $r6 := java.lang.StringBuilder#toString($r5);
        if ($Exception != null) {
            return;
        }
        r := $r6;
        return;
    
}

procedure examples.arrayList.ArrayList#rangeCheck$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $i1 : int;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.arrayList.ArrayList#rangeCheck$int_0:
        r0 := $this;
        
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        if (i0 < $i1) {
            goto examples.arrayList.ArrayList#rangeCheck$int_2;
        }
    
    examples.arrayList.ArrayList#rangeCheck$int_1:
        call $r1 := Alloc();
        call $r2 := examples.arrayList.ArrayList#outOfBoundsMsg$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        call java.lang.IndexOutOfBoundsException#?init?$java.lang.String($r1, $r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.arrayList.ArrayList#rangeCheck$int_2:
        return;
    
}

procedure examples.arrayList.ArrayList#rangeCheckForAdd$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $i1 : int;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.arrayList.ArrayList#rangeCheckForAdd$int_0:
        r0 := $this;
        
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        if (i0 > $i1) {
            goto examples.arrayList.ArrayList#rangeCheckForAdd$int_2;
        }
    
    examples.arrayList.ArrayList#rangeCheckForAdd$int_1:
        if (i0 >= 0) {
            goto examples.arrayList.ArrayList#rangeCheckForAdd$int_3;
        }
    
    examples.arrayList.ArrayList#rangeCheckForAdd$int_2:
        call $r1 := Alloc();
        call $r2 := examples.arrayList.ArrayList#outOfBoundsMsg$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        call java.lang.IndexOutOfBoundsException#?init?$java.lang.String($r1, $r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.arrayList.ArrayList#rangeCheckForAdd$int_3:
        return;
    
}

procedure examples.arrayList.ArrayList#remove$int($this : Ref, i0 : int) returns (r : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var i1 : int;
    var $i2 : int;
    var $i3 : int;
    var $i4 : int;
    var $i5 : int;
    var $r3 : Ref;
    var $i6 : int;
    var $r4 : Ref;
    var $r5 : Ref;
    var $i7 : int;
    var $i8 : int;
    

    examples.arrayList.ArrayList#remove$int_0:
        r0 := $this;
        
        call examples.arrayList.ArrayList#rangeCheck$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $i2 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#modCount));
        $i3 := $i2 + 1;
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#modCount, Int2Union($i3));
        call r1 := examples.arrayList.ArrayList#elementData$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $i4 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $i5 := $i4 - i0;
        i1 := $i5 - 1;
        if (i1 <= 0) {
            goto examples.arrayList.ArrayList#remove$int_2;
        }
    
    examples.arrayList.ArrayList#remove$int_1:
        $r3 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i6 := i0 + 1;
        $r4 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        call java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int($r3, $i6, $r4, i0, i1);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ArrayList#remove$int_2:
        $r5 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        $i7 := Union2Int(Read($Heap, r0, examples.arrayList.ArrayList#size));
        $i8 := $i7 - 1;
        assume Union2Int(Int2Union($i8)) == $i8;
        $Heap := Write($Heap, r0, examples.arrayList.ArrayList#size, Int2Union($i8));
        assert $r5 != null;
        $ArrayContents := $ArrayContents[$r5 := $ArrayContents[$r5][$i8 := null]];
        r := r1;
        return;
    
}

procedure examples.arrayList.ArrayList#remove_pre$int($this : Ref, i0 : int) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ArrayList#remove_pre$int_0:
        r0 := $this;
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList#remove_pre$int_2;
        }
    
    examples.arrayList.ArrayList#remove_pre$int_1:
        $z0 := true;
        goto examples.arrayList.ArrayList#remove_pre$int_3;
    
    examples.arrayList.ArrayList#remove_pre$int_2:
        $z0 := false;
    
    examples.arrayList.ArrayList#remove_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ArrayList#set$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var r2 : Ref;
    var $r3 : Ref;
    

    examples.arrayList.ArrayList#set$int$java.lang.Object_0:
        r0 := $this;
        
        
        call examples.arrayList.ArrayList#rangeCheck$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        call r2 := examples.arrayList.ArrayList#elementData$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        $r3 := Read($Heap, r0, examples.arrayList.ArrayList#elementData);
        assert $r3 != null;
        $ArrayContents := $ArrayContents[$r3 := $ArrayContents[$r3][i0 := r1]];
        r := r2;
        return;
    
}

procedure examples.arrayList.ArrayList#set_pre$int$java.lang.Object($this : Ref, i0 : int, r1 : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ArrayList#set_pre$int$java.lang.Object_0:
        r0 := $this;
        
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList#set_pre$int$java.lang.Object_2;
        }
    
    examples.arrayList.ArrayList#set_pre$int$java.lang.Object_1:
        $z0 := true;
        goto examples.arrayList.ArrayList#set_pre$int$java.lang.Object_3;
    
    examples.arrayList.ArrayList#set_pre$int$java.lang.Object_2:
        $z0 := false;
    
    examples.arrayList.ArrayList#set_pre$int$java.lang.Object_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ArrayList.ArrayList_pre$int(i0 : int) returns (r : bool)
{
    var $z0 : bool;
    

    examples.arrayList.ArrayList.ArrayList_pre$int_0:
        
        if (i0 < 0) {
            goto examples.arrayList.ArrayList.ArrayList_pre$int_2;
        }
    
    examples.arrayList.ArrayList.ArrayList_pre$int_1:
        $z0 := true;
        goto examples.arrayList.ArrayList.ArrayList_pre$int_3;
    
    examples.arrayList.ArrayList.ArrayList_pre$int_2:
        $z0 := false;
    
    examples.arrayList.ArrayList.ArrayList_pre$int_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int($this : Ref, r1 : Ref, i0 : int)
{
    var r0 : Ref;
    var $i1 : int;
    

    examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int_0:
        r0 := $this;
        
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#lastRet, Int2Union(-1));
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#arrayList, r1);
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#cursor, Int2Union(i0));
        $i1 := Union2Int(Read($Heap, r1, examples.arrayList.ArrayList#modCount));
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#expectedModCount, Int2Union($i1));
        return;
    
}

procedure examples.arrayList.ListIterator#add$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var i0 : int;
    var $r2 : Ref;
    var $i1 : int;
    var $r3 : Ref;
    var $i2 : int;
    var $r4 : Ref;
    var r5 : Ref;
    var $r6 : Ref;
    

    examples.arrayList.ListIterator#add$java.lang.Object_0:
        r0 := $this;
        
        call examples.arrayList.ListIterator#checkForComodification(r0);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#add$java.lang.Object_1:
        i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
    
    examples.arrayList.ListIterator#add$java.lang.Object_2:
        $r2 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
    
    examples.arrayList.ListIterator#add$java.lang.Object_3:
        call examples.arrayList.ArrayList#add$int$java.lang.Object($r2, i0, r1);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#add$java.lang.Object_4:
        $i1 := i0 + 1;
    
    examples.arrayList.ListIterator#add$java.lang.Object_5:
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#cursor, Int2Union($i1));
    
    examples.arrayList.ListIterator#add$java.lang.Object_6:
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#lastRet, Int2Union(-1));
    
    examples.arrayList.ListIterator#add$java.lang.Object_7:
        $r3 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
    
    examples.arrayList.ListIterator#add$java.lang.Object_8:
        $i2 := Union2Int(Read($Heap, $r3, examples.arrayList.ArrayList#modCount));
    
    examples.arrayList.ListIterator#add$java.lang.Object_9:
        assume Union2Int(Int2Union($i2)) == $i2;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#expectedModCount, Int2Union($i2));
    
    examples.arrayList.ListIterator#add$java.lang.Object_10:
        goto examples.arrayList.ListIterator#add$java.lang.Object_12;
    
    examples.arrayList.ListIterator#add$java.lang.Object_11:
        $r4 := $Exception;
        r5 := $r4;
        call $r6 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r6);
        if ($Exception != null) {
            return;
        }
        $Exception := $r6;
        return;
    
    examples.arrayList.ListIterator#add$java.lang.Object_12:
        return;
    
}

procedure examples.arrayList.ListIterator#checkForComodification($this : Ref)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r2 : Ref;
    

    examples.arrayList.ListIterator#checkForComodification_0:
        r0 := $this;
        $r1 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i0 := Union2Int(Read($Heap, $r1, examples.arrayList.ArrayList#modCount));
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#expectedModCount));
        if ($i0 == $i1) {
            goto examples.arrayList.ListIterator#checkForComodification_2;
        }
    
    examples.arrayList.ListIterator#checkForComodification_1:
        call $r2 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.arrayList.ListIterator#checkForComodification_2:
        return;
    
}

procedure examples.arrayList.ListIterator#hasNext($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $r1 : Ref;
    var $i1 : int;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#hasNext_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        $r1 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i1 := Union2Int(Read($Heap, $r1, examples.arrayList.ArrayList#size));
        if ($i0 == $i1) {
            goto examples.arrayList.ListIterator#hasNext_2;
        }
    
    examples.arrayList.ListIterator#hasNext_1:
        $z0 := true;
        goto examples.arrayList.ListIterator#hasNext_3;
    
    examples.arrayList.ListIterator#hasNext_2:
        $z0 := false;
    
    examples.arrayList.ListIterator#hasNext_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#hasPrevious($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#hasPrevious_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        if ($i0 == 0) {
            goto examples.arrayList.ListIterator#hasPrevious_2;
        }
    
    examples.arrayList.ListIterator#hasPrevious_1:
        $z0 := true;
        goto examples.arrayList.ListIterator#hasPrevious_3;
    
    examples.arrayList.ListIterator#hasPrevious_2:
        $z0 := false;
    
    examples.arrayList.ListIterator#hasPrevious_3:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#inv($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    var $z0 : bool;
    var $i0 : int;
    var $i1 : int;
    var $r3 : Ref;
    var $i2 : int;
    var $i3 : int;
    var $i4 : int;
    var $r4 : Ref;
    var $i5 : int;
    var $i6 : int;
    var $i7 : int;
    var $i8 : int;
    var $i9 : int;
    var $i10 : int;
    var $i11 : int;
    var $i12 : int;
    var $i13 : int;
    var $z1 : bool;
    

    examples.arrayList.ListIterator#inv_0:
        r0 := $this;
        $r1 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        if ($r1 == null) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_1:
        $r2 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        call $z0 := examples.arrayList.ArrayList#inv($r2);
        if ($Exception != null) {
            return;
        }
        if ($z0 == false) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_2:
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#expectedModCount));
        if ($i0 < 0) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_3:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#expectedModCount));
        $r3 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i2 := Union2Int(Read($Heap, $r3, examples.arrayList.ArrayList#modCount));
        if ($i1 > $i2) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_4:
        $i3 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        if ($i3 < 0) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_5:
        $i4 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        $r4 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i5 := Union2Int(Read($Heap, $r4, examples.arrayList.ArrayList#size));
        if ($i4 > $i5) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_6:
        $i6 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        if ($i6 == -1) {
            goto examples.arrayList.ListIterator#inv_11;
        }
    
    examples.arrayList.ListIterator#inv_7:
        $i7 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        if ($i7 != 0) {
            goto examples.arrayList.ListIterator#inv_9;
        }
    
    examples.arrayList.ListIterator#inv_8:
        $i8 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        $i9 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        if ($i8 == $i9) {
            goto examples.arrayList.ListIterator#inv_11;
        }
    
    examples.arrayList.ListIterator#inv_9:
        $i10 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        if ($i10 <= 0) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_10:
        $i11 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        $i12 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        $i13 := $i12 - 1;
        if ($i11 != $i13) {
            goto examples.arrayList.ListIterator#inv_12;
        }
    
    examples.arrayList.ListIterator#inv_11:
        $z1 := true;
        goto examples.arrayList.ListIterator#inv_13;
    
    examples.arrayList.ListIterator#inv_12:
        $z1 := false;
    
    examples.arrayList.ListIterator#inv_13:
        r := $z1;
        return;
    
}

procedure examples.arrayList.ListIterator#next($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var i0 : int;
    var r1 : Ref;
    var $r2 : Ref;
    var $i2 : int;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i3 : int;
    var $r5 : Ref;
    var $i4 : int;
    var $r6 : Ref;
    

    examples.arrayList.ListIterator#next_0:
        r0 := $this;
        call examples.arrayList.ListIterator#checkForComodification(r0);
        if ($Exception != null) {
            return;
        }
        i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        $r2 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i2 := Union2Int(Read($Heap, $r2, examples.arrayList.ArrayList#size));
        if (i0 < $i2) {
            goto examples.arrayList.ListIterator#next_2;
        }
    
    examples.arrayList.ListIterator#next_1:
        call $r3 := Alloc();
        call java.util.NoSuchElementException#?init?($r3);
        if ($Exception != null) {
            return;
        }
        $Exception := $r3;
        return;
    
    examples.arrayList.ListIterator#next_2:
        $r4 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        r1 := Read($Heap, $r4, examples.arrayList.ArrayList#elementData);
        $i3 := $ArrayLength(r1);
        if (i0 < $i3) {
            goto examples.arrayList.ListIterator#next_4;
        }
    
    examples.arrayList.ListIterator#next_3:
        call $r5 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r5);
        if ($Exception != null) {
            return;
        }
        $Exception := $r5;
        return;
    
    examples.arrayList.ListIterator#next_4:
        $i4 := i0 + 1;
        assume Union2Int(Int2Union($i4)) == $i4;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#cursor, Int2Union($i4));
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#lastRet, Int2Union(i0));
        $r6 := $ArrayContents[r1][i0];
        r := $r6;
        return;
    
}

procedure examples.arrayList.ListIterator#nextIndex($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $i0 : int;
    

    examples.arrayList.ListIterator#nextIndex_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        r := $i0;
        return;
    
}

procedure examples.arrayList.ListIterator#next_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#next_pre_0:
        r0 := $this;
        call $z0 := examples.arrayList.ListIterator#hasNext(r0);
        if ($Exception != null) {
            return;
        }
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#previous($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var i0 : int;
    var r1 : Ref;
    var $i2 : int;
    var $r2 : Ref;
    var $r3 : Ref;
    var $i3 : int;
    var $r4 : Ref;
    var $r5 : Ref;
    

    examples.arrayList.ListIterator#previous_0:
        r0 := $this;
        call examples.arrayList.ListIterator#checkForComodification(r0);
        if ($Exception != null) {
            return;
        }
        $i2 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        i0 := $i2 - 1;
        if (i0 >= 0) {
            goto examples.arrayList.ListIterator#previous_2;
        }
    
    examples.arrayList.ListIterator#previous_1:
        call $r2 := Alloc();
        call java.util.NoSuchElementException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.arrayList.ListIterator#previous_2:
        $r3 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        r1 := Read($Heap, $r3, examples.arrayList.ArrayList#elementData);
        $i3 := $ArrayLength(r1);
        if (i0 < $i3) {
            goto examples.arrayList.ListIterator#previous_4;
        }
    
    examples.arrayList.ListIterator#previous_3:
        call $r4 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r4);
        if ($Exception != null) {
            return;
        }
        $Exception := $r4;
        return;
    
    examples.arrayList.ListIterator#previous_4:
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#cursor, Int2Union(i0));
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#lastRet, Int2Union(i0));
        $r5 := $ArrayContents[r1][i0];
        r := $r5;
        return;
    
}

procedure examples.arrayList.ListIterator#previousIndex($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    

    examples.arrayList.ListIterator#previousIndex_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#cursor));
        $i1 := $i0 - 1;
        r := $i1;
        return;
    
}

procedure examples.arrayList.ListIterator#previous_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#previous_pre_0:
        r0 := $this;
        call $z0 := examples.arrayList.ListIterator#hasPrevious(r0);
        if ($Exception != null) {
            return;
        }
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#remove($this : Ref)
{
    var r0 : Ref;
    var r1 : Ref;
    var $i0 : int;
    var $r2 : Ref;
    var $r3 : Ref;
    var $i1 : int;
    var $i2 : int;
    var $r5 : Ref;
    var $i3 : int;
    var $r6 : Ref;
    var $r7 : Ref;
    var $ret$461129530 : Ref;

    examples.arrayList.ListIterator#remove_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        if ($i0 >= 0) {
            goto examples.arrayList.ListIterator#remove_2;
        }
    
    examples.arrayList.ListIterator#remove_1:
        call $r2 := Alloc();
        call java.lang.IllegalStateException#?init?($r2);
        if ($Exception != null) {
            return;
        }
        $Exception := $r2;
        return;
    
    examples.arrayList.ListIterator#remove_2:
        call examples.arrayList.ListIterator#checkForComodification(r0);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#remove_3:
        $r3 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
    
    examples.arrayList.ListIterator#remove_4:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
    
    examples.arrayList.ListIterator#remove_5:
        call $ret$461129530 := examples.arrayList.ArrayList#remove$int($r3, $i1);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#remove_6:
        $i2 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
    
    examples.arrayList.ListIterator#remove_7:
        assume Union2Int(Int2Union($i2)) == $i2;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#cursor, Int2Union($i2));
    
    examples.arrayList.ListIterator#remove_8:
        assume Union2Int(Int2Union(-1)) == -1;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#lastRet, Int2Union(-1));
    
    examples.arrayList.ListIterator#remove_9:
        $r5 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
    
    examples.arrayList.ListIterator#remove_10:
        $i3 := Union2Int(Read($Heap, $r5, examples.arrayList.ArrayList#modCount));
    
    examples.arrayList.ListIterator#remove_11:
        assume Union2Int(Int2Union($i3)) == $i3;
        $Heap := Write($Heap, r0, examples.arrayList.ListIterator#expectedModCount, Int2Union($i3));
    
    examples.arrayList.ListIterator#remove_12:
        goto examples.arrayList.ListIterator#remove_14;
    
    examples.arrayList.ListIterator#remove_13:
        $r6 := $Exception;
        r1 := $r6;
        call $r7 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r7);
        if ($Exception != null) {
            return;
        }
        $Exception := $r7;
        return;
    
    examples.arrayList.ListIterator#remove_14:
        return;
    
}

procedure examples.arrayList.ListIterator#remove_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r1 : Ref;
    var $i2 : int;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#remove_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        if ($i0 < 0) {
            goto examples.arrayList.ListIterator#remove_pre_3;
        }
    
    examples.arrayList.ListIterator#remove_pre_1:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        $r1 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i2 := Union2Int(Read($Heap, $r1, examples.arrayList.ArrayList#size));
        if ($i1 >= $i2) {
            goto examples.arrayList.ListIterator#remove_pre_3;
        }
    
    examples.arrayList.ListIterator#remove_pre_2:
        $z0 := true;
        goto examples.arrayList.ListIterator#remove_pre_4;
    
    examples.arrayList.ListIterator#remove_pre_3:
        $z0 := false;
    
    examples.arrayList.ListIterator#remove_pre_4:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator#set$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var r2 : Ref;
    var $i0 : int;
    var $r3 : Ref;
    var $r4 : Ref;
    var $i1 : int;
    var $r6 : Ref;
    var $r7 : Ref;
    var $ret$1406206626 : Ref;

    examples.arrayList.ListIterator#set$java.lang.Object_0:
        r0 := $this;
        
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        if ($i0 >= 0) {
            goto examples.arrayList.ListIterator#set$java.lang.Object_2;
        }
    
    examples.arrayList.ListIterator#set$java.lang.Object_1:
        call $r3 := Alloc();
        call java.lang.IllegalStateException#?init?($r3);
        if ($Exception != null) {
            return;
        }
        $Exception := $r3;
        return;
    
    examples.arrayList.ListIterator#set$java.lang.Object_2:
        call examples.arrayList.ListIterator#checkForComodification(r0);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#set$java.lang.Object_3:
        $r4 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
    
    examples.arrayList.ListIterator#set$java.lang.Object_4:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
    
    examples.arrayList.ListIterator#set$java.lang.Object_5:
        call $ret$1406206626 := examples.arrayList.ArrayList#set$int$java.lang.Object($r4, $i1, r1);
        if ($Exception != null) {
            return;
        }
    
    examples.arrayList.ListIterator#set$java.lang.Object_6:
        goto examples.arrayList.ListIterator#set$java.lang.Object_8;
    
    examples.arrayList.ListIterator#set$java.lang.Object_7:
        $r6 := $Exception;
        r2 := $r6;
        call $r7 := Alloc();
        call java.util.ConcurrentModificationException#?init?($r7);
        if ($Exception != null) {
            return;
        }
        $Exception := $r7;
        return;
    
    examples.arrayList.ListIterator#set$java.lang.Object_8:
        return;
    
}

procedure examples.arrayList.ListIterator#set_pre($this : Ref) returns (r : bool)
{
    var r0 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r1 : Ref;
    var $i2 : int;
    var $z0 : bool;
    

    examples.arrayList.ListIterator#set_pre_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        if ($i0 < 0) {
            goto examples.arrayList.ListIterator#set_pre_3;
        }
    
    examples.arrayList.ListIterator#set_pre_1:
        $i1 := Union2Int(Read($Heap, r0, examples.arrayList.ListIterator#lastRet));
        $r1 := Read($Heap, r0, examples.arrayList.ListIterator#arrayList);
        $i2 := Union2Int(Read($Heap, $r1, examples.arrayList.ArrayList#size));
        if ($i1 >= $i2) {
            goto examples.arrayList.ListIterator#set_pre_3;
        }
    
    examples.arrayList.ListIterator#set_pre_2:
        $z0 := true;
        goto examples.arrayList.ListIterator#set_pre_4;
    
    examples.arrayList.ListIterator#set_pre_3:
        $z0 := false;
    
    examples.arrayList.ListIterator#set_pre_4:
        r := $z0;
        return;
    
}

procedure examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int(r0 : Ref, i0 : int) returns (r : bool)
{
    var $z0 : bool;
    var $z1 : bool;
    var $z2 : bool;
    

    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_0:
        
        
        if (r0 == null) {
            goto examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_4;
        }
    
    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_1:
        call $z0 := examples.arrayList.ArrayList#inv(r0);
        if ($Exception != null) {
            return;
        }
        if ($z0 == false) {
            goto examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_4;
        }
    
    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_2:
        call $z1 := examples.arrayList.ArrayList#listiterator_pre$int(r0, i0);
        if ($Exception != null) {
            return;
        }
        if ($z1 == false) {
            goto examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_4;
        }
    
    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_3:
        $z2 := true;
        goto examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_5;
    
    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_4:
        $z2 := false;
    
    examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int_5:
        r := $z2;
        return;
    
}

procedure examples.unannotated.GenericStack#?init?($this : Ref)
{
    var r0 : Ref;
    

    examples.unannotated.GenericStack#?init?_0:
        r0 := $this;
        call examples.unannotated.GenericStack#?init?$int(r0, 5);
        if ($Exception != null) {
            return;
        }
        return;
    
}

procedure examples.unannotated.GenericStack#?init?$int($this : Ref, i0 : int)
{
    var r0 : Ref;
    var $r1 : Ref;
    var $r2 : Ref;
    

    examples.unannotated.GenericStack#?init?$int_0:
        r0 := $this;
        
        call java.lang.Object#?init?(r0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(0)) == 0;
        $Heap := Write($Heap, r0, examples.unannotated.GenericStack#size, Int2Union(0));
        if (i0 > 0) {
            goto examples.unannotated.GenericStack#?init?$int_2;
        }
    
    examples.unannotated.GenericStack#?init?$int_1:
        call $r1 := Alloc();
        call java.lang.IllegalArgumentException#?init?($r1);
        if ($Exception != null) {
            return;
        }
        $Exception := $r1;
        return;
    
    examples.unannotated.GenericStack#?init?$int_2:
        assume Union2Int(Int2Union(i0)) == i0;
        $Heap := Write($Heap, r0, examples.unannotated.GenericStack#capacity, Int2Union(i0));
        call $r2 := Alloc();
        assume $ArrayLength($r2) == i0;
        $Heap := Write($Heap, r0, examples.unannotated.GenericStack#data, $r2);
        return;
    
}

procedure examples.unannotated.GenericStack#Pop($this : Ref) returns (r : Ref)
{
    var r0 : Ref;
    var $r2 : Ref;
    var $i0 : int;
    var $i1 : int;
    var $r3 : Ref;
    

    examples.unannotated.GenericStack#Pop_0:
        r0 := $this;
        $r2 := Read($Heap, r0, examples.unannotated.GenericStack#data);
        $i0 := Union2Int(Read($Heap, r0, examples.unannotated.GenericStack#size));
        $i1 := $i0 - 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.unannotated.GenericStack#size, Int2Union($i1));
        $r3 := $ArrayContents[$r2][$i1];
        r := $r3;
        return;
    
}

procedure examples.unannotated.GenericStack#Push$java.lang.Object($this : Ref, r1 : Ref)
{
    var r0 : Ref;
    var $i0 : int;
    var $r3 : Ref;
    var $i1 : int;
    

    examples.unannotated.GenericStack#Push$java.lang.Object_0:
        r0 := $this;
        
        $r3 := Read($Heap, r0, examples.unannotated.GenericStack#data);
        $i0 := Union2Int(Read($Heap, r0, examples.unannotated.GenericStack#size));
        $i1 := $i0 + 1;
        assume Union2Int(Int2Union($i1)) == $i1;
        $Heap := Write($Heap, r0, examples.unannotated.GenericStack#size, Int2Union($i1));
        assert $r3 != null;
        $ArrayContents := $ArrayContents[$r3 := $ArrayContents[$r3][$i0 := r1]];
        return;
    
}

procedure examples.unannotated.GenericStack#getSize($this : Ref) returns (r : int)
{
    var r0 : Ref;
    var $i0 : int;
    

    examples.unannotated.GenericStack#getSize_0:
        r0 := $this;
        $i0 := Union2Int(Read($Heap, r0, examples.unannotated.GenericStack#size));
        r := $i0;
        return;
    
}

procedure {:extern} java.lang.IllegalArgumentException#?init?($this : Ref);

procedure {:extern} java.lang.IllegalArgumentException#?init?$java.lang.String($this : Ref, param00 : Ref);

procedure {:extern} java.lang.IllegalStateException#?init?($this : Ref);

procedure {:extern} java.lang.IndexOutOfBoundsException#?init?($this : Ref);

procedure {:extern} java.lang.IndexOutOfBoundsException#?init?$java.lang.String($this : Ref, param00 : Ref);

procedure {:extern} java.lang.Integer.valueOf$int(param00 : int) returns (r : Ref);

procedure {:extern} java.lang.Object#?init?($this : Ref);

procedure {:extern} java.lang.RuntimeException#?init?($this : Ref);

procedure {:extern} java.lang.StringBuilder#?init?($this : Ref);

procedure {:extern} java.lang.StringBuilder#append$int($this : Ref, param00 : int) returns (r : Ref);

procedure {:extern} java.lang.StringBuilder#append$java.lang.String($this : Ref, param00 : Ref) returns (r : Ref);

procedure {:extern} java.lang.StringBuilder#toString($this : Ref) returns (r : Ref);

procedure {:extern} java.lang.System.arraycopy$java.lang.Object$int$java.lang.Object$int$int(param00 : Ref, param01 : int, param02 : Ref, param03 : int, param04 : int);

procedure {:extern} java.util.AbstractCollection#?init?($this : Ref);

// Skipping hardcoded method java.util.ArrayList#?init?

// Skipping hardcoded method java.util.ArrayList#?init?$int

// Skipping hardcoded method java.util.ArrayList#add$java.lang.Object

// Skipping hardcoded method java.util.ArrayList#clear

// Skipping hardcoded method java.util.ArrayList#remove$int

// Skipping hardcoded method java.util.ArrayList#remove$java.lang.Object

procedure {:extern} java.util.Arrays.copyOf$Ref$int(param00 : Ref, param01 : int) returns (r : Ref);

// Skipping hardcoded method java.util.Collection#add$java.lang.Object

// Skipping hardcoded method java.util.Collection#clear

// Skipping hardcoded method java.util.Collection#remove$java.lang.Object

procedure {:extern} java.util.ConcurrentModificationException#?init?($this : Ref);

// Skipping hardcoded method java.util.LinkedList#?init?

// Skipping hardcoded method java.util.LinkedList#add$java.lang.Object

// Skipping hardcoded method java.util.LinkedList#clear

// Skipping hardcoded method java.util.LinkedList#remove$int

// Skipping hardcoded method java.util.LinkedList#remove$java.lang.Object

// Skipping hardcoded method java.util.List#add$java.lang.Object

// Skipping hardcoded method java.util.List#clear

// Skipping hardcoded method java.util.List#remove$int

// Skipping hardcoded method java.util.List#remove$java.lang.Object

procedure {:extern} java.util.NoSuchElementException#?init?($this : Ref);


procedure transition_breaks_invariant_________from_________ListIterator?examples.arrayList.ArrayList?int?_________via_________ListIterator?examples.arrayList.ArrayList?int?(examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0 : Ref, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1 : int) {

    var $this : Ref;
    var ret_examples.arrayList.ListIterator#inv : bool;
    var params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int : bool;
    
    
    call initialize_globals();
    
    
    
    
    
    assume (true);
    
    
    call params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int := examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int(examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1);
    assume $Exception == null;
    assume params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int;
    
    
    call $this := Alloc();
    call examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int($this, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    assert ret_examples.arrayList.ListIterator#inv;
    
}

procedure transition_________from_________ListIterator?examples.arrayList.ArrayList?int?_________via_________ListIterator?examples.arrayList.ArrayList?int?_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____next??____remove??____previous??__________________not_throwing(examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0 : Ref, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1 : int) {

    var $this : Ref;
    var ret_examples.arrayList.ListIterator#inv : bool;
    var params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    
    
    
    assume (true);
    
    
    call params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int := examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int(examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1);
    assume $Exception == null;
    assume params_pre_ret_examples.arrayList.ListIterator.ListIterator_pre$examples.arrayList.ArrayList$int;
    
    
    call $this := Alloc();
    call examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int($this, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg0, examples.arrayList.ListIterator#?init?$examples.arrayList.ArrayList$int$arg1);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________nextIndex??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#nextIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#nextIndex := examples.arrayList.ListIterator#nextIndex($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________next??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#next : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#next := examples.arrayList.ListIterator#next($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________hasNext??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasNext : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasNext := examples.arrayList.ListIterator#hasNext($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________checkForComodification??_________testing_________remove??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call examples.arrayList.ListIterator#checkForComodification($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#remove_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________next??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#next : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#next := examples.arrayList.ListIterator#next($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________checkForComodification??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call examples.arrayList.ListIterator#checkForComodification($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________previous??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#previous : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#previous := examples.arrayList.ListIterator#previous($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________add?java.lang.Object?_________testing_________set?java.lang.Object?($this : Ref, examples.arrayList.ListIterator#add$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call examples.arrayList.ListIterator#add$java.lang.Object($this, examples.arrayList.ListIterator#add$java.lang.Object$arg0);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________next??_________testing_________remove??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#next : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#next := examples.arrayList.ListIterator#next($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#remove_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________hasNext??_________testing_________previous??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasNext : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasNext := examples.arrayList.ListIterator#hasNext($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#previous_pre;
    
}

procedure exception_breaks_invariant_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________checkForComodification??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call examples.arrayList.ListIterator#checkForComodification($this);
    assume $Exception != null;$Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    assert ret_examples.arrayList.ListIterator#inv;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??_________via_________nextIndex??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#nextIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#nextIndex := examples.arrayList.ListIterator#nextIndex($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________add?java.lang.Object?_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____previous??__________________not_throwing($this : Ref, examples.arrayList.ListIterator#add$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call examples.arrayList.ListIterator#add$java.lang.Object($this, examples.arrayList.ListIterator#add$java.lang.Object$arg0);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??_________via_________add?java.lang.Object?_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??__________________not_throwing($this : Ref, examples.arrayList.ListIterator#add$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call examples.arrayList.ListIterator#add$java.lang.Object($this, examples.arrayList.ListIterator#add$java.lang.Object$arg0);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??_________via_________next??_________testing_________previous??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#next : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#next := examples.arrayList.ListIterator#next($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#previous_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____next??____remove??_________via_________hasPrevious??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasPrevious : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasPrevious := examples.arrayList.ListIterator#hasPrevious($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____remove??____previous??_________via_________hasNext??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasNext : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasNext := examples.arrayList.ListIterator#hasNext($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____next??____previous??_________via_________hasPrevious??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasPrevious : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasPrevious := examples.arrayList.ListIterator#hasPrevious($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____previous??_________via_________previous??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#previous : Ref;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#previous := examples.arrayList.ListIterator#previous($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____remove??____previous??_________via_________hasNext??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasNext : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasNext := examples.arrayList.ListIterator#hasNext($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____remove??_________via_________nextIndex??_________testing_________next??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#nextIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#nextIndex := examples.arrayList.ListIterator#nextIndex($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____previous??_________via_________nextIndex??_________testing_________previous??($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#nextIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#nextIndex := examples.arrayList.ListIterator#nextIndex($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#previous_pre;
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________hasNext??_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____next??____checkForComodification??____previous??____hasPrevious??__________________not_throwing($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#hasNext : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#hasNext := examples.arrayList.ListIterator#hasNext($this);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
}

procedure necessarily_enabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____next??____remove??_________via_________previousIndex??_________testing_________set?java.lang.Object?($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#previousIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call ret_examples.arrayList.ListIterator#previousIndex := examples.arrayList.ListIterator#previousIndex($this);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert state_pre_ret_examples.arrayList.ListIterator#set_pre;
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________nextIndex??_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____next??____checkForComodification??____previous??____hasPrevious??__________________throwing($this : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var ret_examples.arrayList.ListIterator#nextIndex : int;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call ret_examples.arrayList.ListIterator#nextIndex := examples.arrayList.ListIterator#nextIndex($this);
    assume $Exception != null;
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________add?java.lang.Object?_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??__________________throwing($this : Ref, examples.arrayList.ListIterator#add$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call examples.arrayList.ListIterator#add$java.lang.Object($this, examples.arrayList.ListIterator#add$java.lang.Object$arg0);
    assume $Exception != null;
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(!state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
}

procedure transition_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____next??____previous??_________via_________add?java.lang.Object?_________to_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?__________________not_throwing($this : Ref, examples.arrayList.ListIterator#add$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre);
    
    
    call examples.arrayList.ListIterator#add$java.lang.Object($this, examples.arrayList.ListIterator#add$java.lang.Object$arg0);
    assume $Exception == null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    
    
    assert !(state_pre_ret_examples.arrayList.ListIterator#set_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre && !state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____next??____remove??_________via_________set?java.lang.Object?_________testing_________next??($this : Ref, examples.arrayList.ListIterator#set$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#next_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && !state_pre_ret_examples.arrayList.ListIterator#previous_pre);
    
    
    call examples.arrayList.ListIterator#set$java.lang.Object($this, examples.arrayList.ListIterator#set$java.lang.Object$arg0);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

procedure necessarily_disabled_test_________from_________hasNext??____add?java.lang.Object?____nextIndex??____previousIndex??____checkForComodification??____hasPrevious??____set?java.lang.Object?____remove??____previous??_________via_________set?java.lang.Object?_________testing_________next??($this : Ref, examples.arrayList.ListIterator#set$java.lang.Object$arg0 : Ref) {

    var ret_examples.arrayList.ListIterator#inv : bool;
    var state_pre_ret_examples.arrayList.ListIterator#set_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#remove_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#previous_pre : bool;
    var state_pre_ret_examples.arrayList.ListIterator#next_pre : bool;
    
    
    call initialize_globals();
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#set_pre := examples.arrayList.ListIterator#set_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#remove_pre := examples.arrayList.ListIterator#remove_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#previous_pre := examples.arrayList.ListIterator#previous_pre($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assume ret_examples.arrayList.ListIterator#inv;
    assume (state_pre_ret_examples.arrayList.ListIterator#set_pre && state_pre_ret_examples.arrayList.ListIterator#remove_pre && state_pre_ret_examples.arrayList.ListIterator#previous_pre && !state_pre_ret_examples.arrayList.ListIterator#next_pre);
    
    
    call examples.arrayList.ListIterator#set$java.lang.Object($this, examples.arrayList.ListIterator#set$java.lang.Object$arg0);
    $Exception := null;
    
    
    call ret_examples.arrayList.ListIterator#inv := examples.arrayList.ListIterator#inv($this);
    assume $Exception == null;
    
    call state_pre_ret_examples.arrayList.ListIterator#next_pre := examples.arrayList.ListIterator#next_pre($this);
    assume $Exception == null;
    
    
    assert ret_examples.arrayList.ListIterator#inv;
    
    
    assert !state_pre_ret_examples.arrayList.ListIterator#next_pre;
    
}

