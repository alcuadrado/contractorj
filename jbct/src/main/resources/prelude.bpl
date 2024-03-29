type Ref;

type Field;

type Union = Ref;

const unique null: Ref;

type Real;

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

function RealPlus(Real, Real) : Real;

function RealMinus(Real, Real) : Real;

function RealTimes(Real, Real) : Real;

function RealDivide(Real, Real) : Real;

function RealModulus(Real, Real) : Real;

function RealLessThan(Real, Real) : bool;

function RealLessThanOrEqual(Real, Real) : bool;

function RealGreaterThan(Real, Real) : bool;

function RealGreaterThanOrEqual(Real, Real) : bool;

// this should work like the cmpl cmpg
// the is used as if it was the result of r1-r2
// the function is just used to be compared against 0
// we need to axiom in some way that RealCompare(r1,r2) > 0 when r1 > r2 , etc.
function RealCompare(r1: Real, r2: Real) : int;

/*axiom (forall r1: Real, r2 :Real :: RealGreaterThan(r1, r2) ==>  RealCompare(r1,r2) > 0 );
axiom (forall r1: Real, r2 :Real :: RealGreaterThanOrEqual(r1, r2) ==>  RealCompare(r1,r2) >= 0 );
axiom (forall r1: Real, r2 :Real :: RealLessThan(r1, r2) ==>  RealCompare(r1,r2) < 0 );
axiom (forall r1: Real, r2 :Real :: RealLessThanOrEqual(r1,r2) ==>  RealCompare(r1,r2) >= 0 );*/

function BitwiseAnd(int, int) : int;

function BitwiseOr(int, int) : int;
axiom (forall i1: int, i2 :int :: i1 < 0 || i2 < 0 <==> BitwiseOr(i1,i2) < 0 );
axiom (forall i1: int, i2 :int :: i1 >= 0 && i2 >= 0 <==> BitwiseOr(i1,i2) >= 0 );

function BitwiseExclusiveOr(int, int) : int;

//function BitwiseNegation(int) : int; this is handled by XOR op.

function RightShift(int, int) : int;

function LeftShift(int, int) : int;

var $ArrayContents: [Ref][int]Union;

function $ArrayLength(Ref) : int;

function $StringLength(Ref) : int;

function Union2Bool(u: Union) : bool;

function Union2Int(u: Union) : int;

function Union2Real(u: Union) : Real;

function Bool2Union(boolValue: bool) : Union;

function Int2Union(intValue: int) : Union;

function Real2Union(realValue: Real) : Union;

function Int2Real(int) : Real;

function Real2Int(Real) : int;

// this is temporary until types are implemented
procedure InstanceOfTemp() returns ($result : bool) {}

// Array hardcoded methods

procedure java.util.Arrays.copyOf$java.lang.Object??$int(param00 : Ref, param01 : int) returns (r : Ref) {
    call r := Alloc();
    assume $ArrayLength(r) == param01;
}

// String length mock

procedure java.lang.String#length($this : Ref) returns (r : int) {
    r := $StringLength($this);
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

// static variables from classes that are not "parsed" by soot, therefore they are not translated to boogie
// these variables are used within the translated code

// Socket example - these variables are used in java.net package
const unique sun.security.util.SecurityConstants.GET_PROXYSELECTOR_PERMISSION : Ref;
const unique sun.security.util.SecurityConstants.SET_PROXYSELECTOR_PERMISSION : Ref;
const unique java.nio.charset.CodingErrorAction.REPLACE : Ref;
const unique java.text.Normalizer$Form.NFC : Ref;
const unique sun.security.util.SecurityConstants.SPECIFY_HANDLER_PERMISSION : Ref;
const unique sun.security.util.SecurityConstants.ALL_PERMISSION : Ref;

/*function java.net.InetSocketAddress#getAddress_function($this : Ref) returns (r : Ref);
procedure java.net.InetSocketAddress#getAddress($this : Ref) returns (r : Ref) {
    r := java.net.InetSocketAddress#getAddress_function($this);
}*/

procedure examples.Socket.MockSocket.bind$java.net.SocketImpl$java.net.InetAddress$int(param00 : Ref, param01 : Ref, param02 : int)
{
    var r0 : Ref;
    var r1 : Ref;
    var i0 : int;


    r0 := param00;
    r1 := param01;
    i0 := param02;

    examples.Socket.MockSocket.bind$java.net.SocketImpl$java.net.InetAddress$int_0:



        call java.net.SocketImpl#bind$java.net.InetAddress$int(r0, r1, i0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#localport, Int2Union(10));
        return;

}

procedure examples.Socket.MockSocket.connect$java.net.SocketImpl$java.lang.String$int(param00 : Ref, param01 : Ref, param02 : int)
{
    var r0 : Ref;
    var r1 : Ref;
    var i0 : int;


    r0 := param00;
    r1 := param01;
    i0 := param02;

    examples.Socket.MockSocket.connect$java.net.SocketImpl$java.lang.String$int_0:



        call java.net.SocketImpl#connect$java.lang.String$int(r0, r1, i0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#localport, Int2Union(10));
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#port, Int2Union(10));
        return;

}

procedure examples.Socket.MockSocket.connect$java.net.SocketImpl$java.net.InetAddress$int(param00 : Ref, param01 : Ref, param02 : int)
{
    var r0 : Ref;
    var r1 : Ref;
    var i0 : int;


    r0 := param00;
    r1 := param01;
    i0 := param02;

    examples.Socket.MockSocket.connect$java.net.SocketImpl$java.net.InetAddress$int_0:



        call java.net.SocketImpl#connect$java.net.InetAddress$int(r0, r1, i0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#localport, Int2Union(10));
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#port, Int2Union(10));
        return;

}

procedure examples.Socket.MockSocket.connect$java.net.SocketImpl$java.net.SocketAddress$int(param00 : Ref, param01 : Ref, param02 : int)
{
    var r0 : Ref;
    var r1 : Ref;
    var i0 : int;


    r0 := param00;
    r1 := param01;
    i0 := param02;

    examples.Socket.MockSocket.connect$java.net.SocketImpl$java.net.SocketAddress$int_0:



        call java.net.SocketImpl#connect$java.net.SocketAddress$int(r0, r1, i0);
        if ($Exception != null) {
            return;
        }
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#localport, Int2Union(10));
        assume Union2Int(Int2Union(10)) == 10;
        $Heap := Write($Heap, r0, java.net.SocketImpl#port, Int2Union(10));
        return;

}
// mock for StringTokenizer

/*
// Estos mocks los use para cuando seteabamos un limite al tamaño del maxPosition

//function java.lang.String#charAt$int_function($this : Ref, param00 : int) returns (r : int);
//function java.lang.String#indexOf$int_function($this : Ref, param00 : int) returns (r : int);
//function java.lang.String#codePointAt$int_function($this : Ref, param00 : int) returns (r : int);
//function java.lang.Character.charCount$int_function(param00 : int) returns (r : int);

procedure java.lang.Character.charCount$int(param00 : int) returns (r : int){
  assume java.lang.Character.charCount$int_function(param00) == 1 || java.lang.Character.charCount$int_function(param00) == 2 ;
  r := java.lang.Character.charCount$int_function(param00);
}

procedure java.lang.String#codePointAt$int($this : Ref, param00 : int) returns (r : int){
  r := java.lang.String#codePointAt$int_function($this, param00);
}

procedure java.lang.String#charAt$int($this : Ref, param00 : int) returns (r : int){
  assume java.lang.String#charAt$int_function($this, param00) >= 0;
  r := java.lang.String#charAt$int_function($this, param00);
}

procedure java.lang.String#indexOf$int($this : Ref, param00 : int) returns (r : int){
  //assume java.lang.String#indexOf$int$int_function($this, param00 >= 0;
  r := java.lang.String#indexOf$int_function($this, param00);
}

//procedure examples.StringTokenizer.StringTokenizer#isDelimiter$int($this : Ref, param00 : int) returns (r : bool){
//    r := examples.StringTokenizer.StringTokenizer#isDelimiter$int_function($this, param00);
//}

//function examples.StringTokenizer.StringTokenizer#isDelimiter$int_function($this : Ref, param00 : int) returns (r : bool);

*/

procedure examples.StringTokenizer.StringTokenizer#skipDelimiters$int($this : Ref, param00 : int) returns (r : int){
    r := examples.StringTokenizer.StringTokenizer#skipDelimiters$int_function($this, param00);
}

procedure examples.StringTokenizer.StringTokenizer#scanToken$int($this : Ref, param00 : int) returns (r : int){
    r := examples.StringTokenizer.StringTokenizer#scanToken$int_function($this, param00);
}

axiom (forall $ref: Ref, param00 : int :: examples.StringTokenizer.StringTokenizer#scanToken$int_function($ref, param00) >= param00 &&
            examples.StringTokenizer.StringTokenizer#scanToken$int_function($ref, param00) >= 0);

function examples.StringTokenizer.StringTokenizer#skipDelimiters$int_function($this : Ref, param00 : int) returns (r : int);
function examples.StringTokenizer.StringTokenizer#scanToken$int_function($this : Ref, param00 : int) returns (r : int);

axiom (forall $ref: Ref, param00 : int :: examples.StringTokenizer.StringTokenizer#skipDelimiters$int_function($ref, param00) >= param00 &&
            examples.StringTokenizer.StringTokenizer#skipDelimiters$int_function($ref, param00) >= 0);

procedure java.net.SocketImpl#shutdownOutput($this : Ref) {
}

procedure java.net.SocketImpl#shutdownInput($this : Ref) {
}

