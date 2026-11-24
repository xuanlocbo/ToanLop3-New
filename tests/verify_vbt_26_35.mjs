import fs from 'node:fs';
import assert from 'node:assert/strict';
function verify(docs,oldIds){
const lessons=docs.flatMap(d=>d.lessons),all=lessons.flatMap(l=>l.items),errors=[],checks={},manual=[],open=[],samples=[],groupKeys=new Set(),seen=new Set(),positions=new Map(all.map((q,i)=>[q.id,i]));
const ranges=[[66,68],[69,70],[71,73],[74,75],[76,77],[78,79],[80,80],[81,81],[82,84],[85,87]];
const expectedGroups={66:{"1":[1,2,3,4]},67:{"2":[1,2,3,4]},68:{"3":[1,2,3,4]},69:{"1":[1,2,3,4]},70:{"2":[1,2,3,4]},71:{"1":[1,2]},72:{"1":[3],"2":[1]},73:{"2":[2,3,4]},74:{"1":[1,2,3,4]},75:{"2":[1,2,3,4]},76:{"1":[1,2,3,4]},77:{"2":[1,2,3,4]},78:{"1":[1]},79:{"1":[2,3,4]},80:{"1":[1,2,3,4]},81:{"1":[1,2,3,4]},82:{"1":[1,2,3]},83:{"1":[4],"2":[1]},84:{"2":[2,3,4]},85:{"1":[1,2,3]},86:{"1":[4],"2":[1,2]},87:{"2":[3,4]}};
const eq=(a,b)=>JSON.stringify(a)===JSON.stringify(b),seteq=(a,b)=>eq([...a].sort(),[...b].sort());
function assert(ok,id,why){if(!ok)errors.push({id,why});}
function calc(e){const s=e.replace(/\s/g,"").replaceAll("×","*").replaceAll("−","-").replaceAll(":","/");if(!/^[0-9()+*\/-]+$/.test(s))throw Error(e);let output=[],ops=[],prec={"+":1,"-":1,"*":2,"/":2};for(let t of s.match(/\d+|[()+*\/-]/g)){if(/^\d+$/.test(t))output.push(+t);else if(t==="(")ops.push(t);else if(t===")"){while(ops.length&&ops.at(-1)!=="(")output.push(ops.pop());if(ops.pop()!=="(")throw Error("paren "+e);}else{while(ops.length&&prec[ops.at(-1)]>=prec[t])output.push(ops.pop());ops.push(t);}}while(ops.length)output.push(ops.pop());let stack=[];for(let t of output){if(typeof t==="number")stack.push(t);else{let b=stack.pop(),a=stack.pop();stack.push(t==="+"?a+b:t==="-"?a-b:t==="*"?a*b:a/b);}}if(stack.length!==1||!Number.isFinite(stack[0]))throw Error(e);return stack[0];}
function extreme(items,target,value){let v=target==="max"?Math.max(...items.map(value)):Math.min(...items.map(value));return items.filter(i=>value(i)===v);}
function op(a,o,b){return o==="×"?a*b:o===":"?a/b:o==="+"?a+b:a-b;}
for(const q of all){
 const id=q.id,r=ranges[q.lesson-26],a=q.answer,c=q.check,v=q.visual;
 assert(!seen.has(id),id,"duplicate");seen.add(id);
 assert(q.book==="VBT"&&r&&q.page>=r[0]&&q.page<=r[1]&&q.pdfPage===q.page+1&&id===`VBT.${q.lesson}.${q.period}.${q.page}.${q.exercise}.${q.part}`&&q.section===`T${q.period}`,id,"source identity");
 assert(!!q.prompt&&!!q.hint&&!!q.solution,id,"required fields");
 groupKeys.add(`${q.page}.${q.period}.${q.exercise}`);
 if(q.prerequisite)assert(positions.has(q.prerequisite)&&positions.get(q.prerequisite)<positions.get(id),id,"prerequisite order");
 if(q.kind==="choice"||q.kind==="multi"){assert(Array.isArray(v?.options)&&new Set(v.options).size===v.options.length,id,"choice options");assert(q.kind==="multi"?Array.isArray(a)&&a.every(x=>v.options.includes(x))&&new Set(a).size===a.length:v.options.includes(a),id,"choice answer");}
 if(v?.type==="long_division"){let rem=0,start=false,quot=0,steps=[];for(let ch of String(v.dividend)){let p=rem*10+Number(ch),d=Math.floor(p/v.divisor);rem=p%v.divisor;if(!start&&d===0)continue;start=true;quot=quot*10+d;steps.push({partialDividend:p,quotientDigit:d,product:d*v.divisor,remainder:rem});}assert(quot===a.quotient&&rem===a.remainder&&eq(steps,v.steps),id,"long division steps");checks.long_division_steps=(checks.long_division_steps||0)+1;}
 if(!c){if(q.kind==="study"){assert(a===null,id,"sample answer");samples.push(id);}else if(q.kind.startsWith("practice")){assert(a===null,id,"open task fixed answer");open.push(id);}else if(q.reviewMethod){manual.push(id);}else assert(false,id,"missing check");continue;}
 let ok=false;const [x,y,z]=c.args||[];
 switch(c.op){
 case "expression":ok=calc(c.expression)===a;break;
 case "divmod":ok=x===y*a.quotient+a.remainder&&a.remainder>=0&&a.remainder<y;break;
 case "truth":ok=a===((x===z*c.shown.quotient+c.shown.remainder&&c.shown.remainder<z&&c.shown.remainder>=0)?"Đúng":"Sai");break;
 case "remainder":ok=x%y===a;break;
 case "ceil_div":ok=Math.ceil(x/y)===a;break;
 case "floor_div":ok=Math.floor(x/y)===a;break;
 case "operation_word":ok=(a==="Gấp"?c.start*c.factor:c.start/c.factor)===c.end;break;
 case "operation_label":ok=c.candidates.filter(t=>op(c.start,t.op,t.factor)===c.end).map(t=>t.label).includes(a);break;
 case "convert":{let k={mm:1,cm:10,m:1000,g:1,kg:1000,ml:1,l:1000};ok=x*k[y]/k[z]===a;break;}
 case "length_difference":ok=y-x===a&&v.segment.startMm===x&&v.segment.endMm===y;break;
 case "extreme_length":{let o=extreme(c.objects,c.target,i=>i.value*(i.unit==="cm"?10:1));ok=o.length===1&&o[0].name===a;break;}
 case "sum_weights":ok=c.weights.reduce((s,x)=>s+x,0)===a&&eq(c.weights,v.leftWeightsG);break;
 case "read_scale":ok=c.valueG===a&&v.valueG===a&&a>=0&&a<=v.maxG;break;
 case "read_cup":{let cup=c.cups.find(t=>t.id===c.cupId);ok=cup?.valueMl===a&&cup.valueMl<=cup.capacityMl&&eq(c.cups,v.cups);break;}
 case "lower_than_location":{let t=c.items.find(i=>i.name===c.location),lower=c.items.filter(i=>i.value<t.value);ok=lower.length===1&&lower[0].name===a;break;}
 case "extreme_named":{let t=extreme(c.items,c.target,i=>i.value);ok=t.length===1&&t[0].name===a;break;}
 case "temperature_words_to_number":ok=({"Ba mươi lăm độ xê":35,"Ba mươi mốt độ xê":31})[c.words]===a;break;
 case "temperature_number_to_words":ok=c.valueC===15&&a==="Mười lăm độ xê";break;
 case "middle":ok=[...c.args].sort((a,b)=>a-b)[1]===a;break;
 case "sort_values":ok=eq([...c.args].sort((x,y)=>c.direction==="descending"?y-x:x-y),a);break;
 case "single_weighing":{let g=c.weightsG.reduce((x,y)=>x+y,0);ok=(c.keep==="remaining"?c.initialG-g:g)===c.targetG&&v.targetG===c.targetG&&v.weighingsAllowed===1&&v.expectedAction.keep===c.keep&&(c.keep==="remaining"?a==="Cân 300 g, giữ phần còn lại":a==="Cân gạo bằng cả hai quả cân");break;}
 case "read_temperature":ok=c.valueC===a&&v.valueC===a&&a>=v.rangeC[0]&&a<=v.rangeC[1];break;
 case "extreme_cup":{let t=extreme(c.cups,c.target,i=>i.valueMl);ok=t.length===1&&t[0].id===a;break;}
 case "cup_pair_sum":{let pairs=[];for(let i=0;i<c.cups.length;i++)for(let j=i+1;j<c.cups.length;j++)if(c.cups[i].valueMl+c.cups[j].valueMl===c.targetMl)pairs.push([c.cups[i].id,c.cups[j].id]);ok=pairs.length===1&&seteq(pairs[0],a);break;}
 case "pour_sequence":{let cap=c.capacitiesMl,s=[0,0];let valid=true;for(let t of c.actions){if(t.type==="fill")s[t.cup]=cap[t.cup];else if(t.type==="empty")s[t.cup]=0;else if(t.type==="pour"){let d=Math.min(s[t.from],cap[t.to]-s[t.to]);s[t.from]-=d;s[t.to]+=d;}else valid=false;valid&&=s.every((x,i)=>x>=0&&x<=cap[i]);}ok=valid&&s.includes(c.targetMl)&&a.targetMl===c.targetMl&&eq(cap,v.capacitiesMl)&&eq(c.actions,v.solutionActions);break;}
 default:assert(false,id,"unrecognized check "+c.op);
 }
 assert(ok,id,"check "+c.op);checks[c.op]=(checks[c.op]||0)+1;
}
const expected=Object.entries(expectedGroups).flatMap(([p,ps])=>Object.entries(ps).flatMap(([t,es])=>es.map(e=>`${p}.${t}.${e}`)));
assert(seteq([...groupKeys],expected),"all","printed exercise group coverage");
assert(seteq(lessons.map(l=>l.lesson),[26,27,28,29,30,31,32,33,34,35]),"all","lessons");
assert(oldIds.length===195&&oldIds.every(id=>seen.has(id)),"all","legacy record coverage");
assert(all.length===199&&all.filter(q=>q.check).length===187&&manual.length===7&&open.length===3&&samples.length===2,"all","expected counts");
return {errors,checks,groups:groupKeys.size,rows:all.length,oldRecords:oldIds.length,newIds:all.filter(q=>!oldIds.includes(q.id)).map(q=>q.id),manual,open,samples,counts:lessons.map(l=>[l.lesson,l.items.length]),pages:[...new Set(all.map(q=>q.page))],checked:all.filter(q=>q.check).length};
}
const base='app/src/main/assets/curriculum/';
const docs=['vbt_26_29.json','vbt_30_35.json'].map(p=>JSON.parse(fs.readFileSync(base+p,'utf8')));
const oldIds=[...fs.readFileSync('app/src/main/java/vn/mocnhien/toan3/BookBank.java','utf8').matchAll(/^\s*add\((2[6-9]|3[0-5]),(\d+),(\d+),(\d+),(\d+),Kind\./gm)].map(m=>'VBT.'+m.slice(1,6).join('.'));
const result=verify(docs,oldIds);
console.log(JSON.stringify(result,null,2));
assert.equal(result.errors.length,0);
