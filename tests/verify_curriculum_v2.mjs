import assert from 'node:assert/strict';
import {prepare,readSource} from '../tools/prepare_curriculum.mjs';
function verify(data){
const all=data.lessons.flatMap(l=>[...l.sgk,...l.vbt,...l.bonus]),errors=[],stats={},seen=new Set(),manual=[],open=[],study=[],numeric=[],ids=new Map(all.map((q,i)=>[q.id,i]));
const eq=(a,b)=>JSON.stringify(a)===JSON.stringify(b),seteq=(a,b)=>eq(a.map(x=>JSON.stringify(x)).sort(),b.map(x=>JSON.stringify(x)).sort());
const assert=(ok,id,why)=>{if(!ok)errors.push({id,why});};
const sum=a=>a.reduce((s,x)=>s+x,0),prod=a=>a.reduce((s,x)=>s*x,1);
function calc(e){let s=e.replace(/\s/g,"").replaceAll("×","*").replaceAll("−","-").replaceAll(":","/");if(!/^[0-9()+*\/-]+$/.test(s))throw Error("invalid expression "+e);let output=[],ops=[],prec={"+":1,"-":1,"*":2,"/":2};for(let t of s.match(/\d+|[()+*\/-]/g)){if(/^\d+$/.test(t))output.push(+t);else if(t==="(")ops.push(t);else if(t===")"){while(ops.length&&ops.at(-1)!=="(")output.push(ops.pop());if(ops.pop()!=="(")throw Error(e);}else{while(ops.length&&prec[ops.at(-1)]>=prec[t])output.push(ops.pop());ops.push(t);}}while(ops.length)output.push(ops.pop());let stack=[];for(let t of output){if(typeof t==="number")stack.push(t);else{let b=stack.pop(),a=stack.pop();stack.push(t==="+"?a+b:t==="-"?a-b:t==="*"?a*b:a/b);}}if(stack.length!==1||!Number.isFinite(stack[0]))throw Error(e);return stack[0];}
function op(a,o,b){return calc(`${a}${o}${b}`);}
function combinations(n,opts,unique=false){let out=[];function rec(a){if(a.length===n){out.push(a);return;}for(let x of opts)if(!unique||!a.includes(x))rec([...a,x]);}rec([]);return out;}
function substitute(t,values){let i=0;return t.replace(/\?/g,()=>values[i++]);}
function extreme(items,target,value){let v=target==="max"?Math.max(...items.map(value)):Math.min(...items.map(value));return items.filter(x=>value(x)===v);}
const exprs=c=>c.expressions.map(x=>typeof x==="string"?{id:x,expression:x}:x);
function angles(points,segments){let adj=Object.fromEntries(Object.keys(points).map(x=>[x,[]]));for(let [a,b] of segments){adj[a].push(b);adj[b].push(a);}let out=[];for(let [v,ns] of Object.entries(adj)){let o=points[v],rays=[];for(let p of ns){let d=points[p].map((x,i)=>x-o[i]),n=Math.hypot(...d);let r=d.map(x=>x/n);if(!rays.some(t=>t.r.every((x,i)=>Math.abs(x-r[i])<1e-9)))rays.push({p,r});}for(let i=0;i<rays.length;i++)for(let j=i+1;j<rays.length;j++){let a=rays[i],b=rays[j],dot=a.r[0]*b.r[0]+a.r[1]*b.r[1],cross=a.r[0]*b.r[1]-a.r[1]*b.r[0];if(Math.abs(cross)>1e-9)out.push({vertex:v,arms:[a.p,b.p],right:Math.abs(dot)<1e-9});}}return out;}
function angleKey(t,points){let o=points[t.vertex];return t.vertex+":"+t.arms.map(p=>{let d=points[p].map((x,i)=>x-o[i]),n=Math.hypot(...d);return d.map(x=>(x/n).toFixed(7)).join(",");}).sort().join("|");}
function pour(v,a){let caps=v.capacitiesMl,s=[...(v.initialMl||[0,0])];for(let t of v.solutionActions||v.actions){if(t.type==="fill"&&t.cup>=0&&t.cup<caps.length)s[t.cup]=caps[t.cup];else if(t.type==="empty"&&t.cup>=0&&t.cup<caps.length)s[t.cup]=0;else if(t.type==="pour"&&t.from!==t.to&&caps[t.from]&&caps[t.to]){let d=Math.min(s[t.from],caps[t.to]-s[t.to]);s[t.from]-=d;s[t.to]+=d;}else return false;if(s.some((x,i)=>x<0||x>caps[i]))return false;}return s.includes(a.targetMl)&&v.targetMl===a.targetMl;}
function hasWeights(available,chosen){let x=[...available];for(let w of chosen){let i=x.indexOf(w);if(i<0)return false;x.splice(i,1);}return true;}
const unit={mm:1,cm:10,m:1000,g:1,kg:1000,ml:1,l:1000};
const manualKeys={
"SGK.31.88.LT.2.a":{"gà":"2 kg","chó":"20 kg","chim":"200 g","bò":"200 kg"},
"SGK.34.93.HD1.2.a":"20 g","SGK.34.93.HD1.2.b":"400 g","SGK.34.93.HD1.2.c":"3 kg",
"SGK.34.94.HD2.1.a":{"Đọc sách":"B","Quạt vì nóng":"C","Sưởi vì lạnh":"A"},"SGK.34.94.HD2.2.a":{"Quả trứng":"100 g","Tạ":"100 kg","Túi cà chua":"1 kg"},
"SGK.43.119.LT2.2.a":"5 mm","SGK.43.119.LT2.2.b":"20 g","SGK.43.119.LT2.2.c":"15 ml","SGK.43.119.LT2.2.d":"37 °C",
"VBT.34.1.82.2.1":"10 g","VBT.34.1.82.2.2":"1 kg","VBT.34.1.82.2.3":"5 kg","VBT.34.2.84.2.1":"20 kg","VBT.34.2.84.2.2":"2 kg","VBT.34.2.84.2.3":"20 g",
"VBT.35.2.86.2.2":"Có, nhiệt độ cao hơn bình thường","VBT.43.2.111.2.1":"15 mm","VBT.43.2.111.2.2":"8 g","VBT.43.2.111.2.3":"200 ml","VBT.43.2.111.2.4":"25 °C"};
const missingChecks={
"SGK.27.80.LT.2.a":q=>{let v=q.visual;return v.paths[v.options.indexOf(q.answer)].reduce((s,t)=>op(s,t.op,t.value),v.start)===v.target},
"SGK.28.82.HD.2.a":q=>q.answer.intermediate===10-3&&q.answer.final===10+7,
"SGK.28.82.LT.1.a":q=>q.answer.intermediate===10-4&&q.answer.final===10+6,
"SGK.28.82.LT.2.a":q=>q.answer.intermediate===9*2&&q.answer.final===9+18,
"SGK.28.82.LT.3.a":q=>q.answer.intermediate===30+10&&q.answer.final===30+40&&q.reviewRequired===true,
"SGK.29.84.LT2.4.a":q=>q.answer.intermediate===5*3&&q.answer.final===5+15,
"SGK.30.86.HD.3.a":q=>q.answer==="Ve sầu"&&3*10>3,
"SGK.33.91.HD.1.b1":q=>q.answer==="Hà Nội"&&30>26,
"SGK.33.91.HD.1.b2":q=>q.answer==="Sa Pa"&&10<26,
"SGK.33.92.LT.1.a1":q=>q.answer===27,"SGK.33.92.LT.1.a2":q=>q.answer===36,"SGK.33.92.LT.1.a3":q=>q.answer===15,
"SGK.33.92.LT.2.a":q=>seteq(q.answer,[38,37,39].filter(t=>t>37).map(t=>t+" °C")),
"SGK.34.93.HD1.3.a":q=>sum(q.answer.selectedWeightsG)===1000&&hasWeights(q.visual.availableWeightsG,q.answer.selectedWeightsG),
"SGK.34.94.HD2.3.a":q=>extreme(q.visual.cups,"min",x=>x.valueMl)[0].id===q.answer,
"SGK.34.94.HD2.3.b":q=>sum(q.answer.map(id=>q.visual.cups.find(x=>x.id===id).valueMl))===350,
"SGK.34.94.HD2.3.c":q=>sum(q.answer.map(id=>q.visual.cups.find(x=>x.id===id).valueMl))===550,
"SGK.35.95.LT2.2.a":q=>sum(q.visual.scales[0].leftWeightsG)===sum(q.visual.scales[1].leftWeightsG)-sum(q.visual.scales[1].rightWeightsG)&&q.visual.scales[2].relation==="right_heavier"&&q.answer==="C"
};
const sgk=[75,79,81,83,85,87,89,91,93,95,97,99,104,109,111,113,116,118,120,122],vbt=[66,69,71,74,76,78,80,81,82,85,88,90,94,98,100,102,106,109,113,116];
for(let q of all){let id=q.id,a=q.kind==="study"?q.sampleAnswer??q.answer:q.answer,c=q.check,v=q.visual,g=q.grading;
assert(!seen.has(id),id,"duplicate");seen.add(id);
assert(q.lesson>=26&&q.lesson<=44&&q.prompt&&q.hint&&q.solution&&g&&q.source,id,"required");
if(q.book!=="BONUS"){let p=q.book==="SGK"?sgk:vbt;assert(q.page>=p[q.lesson-26]&&q.page<p[q.lesson-25]&&q.pdfPage===q.page+1,id,"source page");}else assert(q.page===null&&q.source.original&&q.reward.coins===(q.exercise===1?80:120),id,"bonus source/reward");
for(let p of q.prerequisites)assert(ids.has(p)&&ids.get(p)<ids.get(id),id,"prerequisite");
assert(q.reward.claimId===id&&q.reward.once===true&&q.reward.coins>=0,id,"reward");
if(q.kind==="study"){study.push(id);assert(q.answer===null&&g.mode==="example"&&!g.scoreEligible&&q.reward.coins===0,id,"example not graded");}
if(q.kind.startsWith("practice")){open.push(id);assert(q.answer===null&&g.mode==="adult_review"&&!g.scoreEligible&&q.reward.coins===0,id,"open not scored");continue;}
if(["choice","multi","segment_multi","match_value"].includes(q.kind)){assert(Array.isArray(v?.options)&&new Set(v.options).size===v.options.length,id,"options");if(v?.options)assert(Array.isArray(a)?a.every(x=>v.options.includes(x))&&new Set(a).size===a.length:v.options.includes(a),id,"option answer");}
if(v?.type==="long_division"){let rem=0,quot=0,started=false,ss=[];for(let ch of String(v.dividend)){let p=rem*10+Number(ch),d=Math.floor(p/v.divisor);rem=p%v.divisor;if(!started&&d===0)continue;started=true;quot=quot*10+d;ss.push({partialDividend:p,quotientDigit:d,product:d*v.divisor,remainder:rem});}assert(quot===a.quotient&&rem===a.remainder&&eq(ss,v.steps),id,"long division steps");stats.long_division_steps=(stats.long_division_steps||0)+1;}
if(v?.type==="long_multiplication"){let carry=0,result=0,place=1,ss=[];for(let d of [...String(v.multiplicand)].map(Number).reverse()){let p=d*v.multiplier+carry;ss.push({digit:d,carryIn:carry,product:p,resultDigit:p%10,carryOut:Math.floor(p/10)});carry=Math.floor(p/10);result+=(p%10)*place;place*=10;}assert(result+carry*place===a&&eq(ss,v.steps),id,"long multiplication steps");stats.long_multiplication_steps=(stats.long_multiplication_steps||0)+1;}
if(!c){if(q.kind==="study")continue;let ok=false;if(Object.hasOwn(manualKeys,id)){manual.push(id);ok=eq(manualKeys[id],a);}else if(missingChecks[id]){ok=missingChecks[id](q);numeric.push(id);}else if(q.kind==="route_match"){let b=v.bridges.find(x=>x.id===a.bridge);ok=b&&op(v.card,b.op,b.value)===a.basket&&v.baskets.includes(a.basket);numeric.push(id);}else if(q.kind==="ruler"){ok=v.segment.endMm-v.segment.startMm===a;numeric.push(id);}else if(q.kind==="balance"){ok=sum(v.leftWeightsG)===a;numeric.push(id);}else if(q.kind==="scale"){ok=v.valueG===a;numeric.push(id);}else if(q.kind==="cup"){ok=v.cups.find(x=>x.id===(q.part==="a1"?"B":"C")).valueMl===a;numeric.push(id);}else if(q.kind==="thermometer"){ok=v.valueC===a;numeric.push(id);}else if(q.kind==="pour"){ok=pour(v,a);numeric.push(id);}else if(q.kind==="board_game"){let board=v.board;ok=board.length===28&&board.filter(x=>x.type==="math").length===20&&new Set(board.map(x=>x.pos.join(","))).size===28&&board.every((x,i)=>x.index===i&&(x.type==="math"?op(x.a,x.o,x.b)===x.z&&all.some(t=>t.visual?.type==="board_tile"&&t.visual.boardIndex===i&&t.answer===x.z):x.type==="animal"?board[x.jump]?.type==="math":x.type==="start"&&i===0));numeric.push(id);}else{assert(false,id,"unclassified answer");continue;}assert(ok,id,"additional verification");continue;}
let ok=false;let [x,y,z]=c.args||[];
switch(c.op){
case "expression":ok=calc(c.expression)===a;break;
case "+":case "−":case "×":case ":":ok=op(x,c.op,y)===a;break;
case "divmod":ok=x===y*a.quotient+a.remainder&&a.remainder>=0&&a.remainder<y;break;
case "filter_remainder":ok=seteq(c.args.filter(([a,b])=>a%b===c.target).map(([a,b])=>`${a} : ${b}`),a);break;
case "ceil_div":ok=Math.ceil(x/y)===a;break;case "floor_div":ok=Math.floor(x/y)===a;break;case "remainder":ok=x%y===a;break;
case "convert":ok=x*unit[y]/unit[z]===a;break;
case "chain":ok=c.args.slice(1).reduce((s,b,i)=>op(s,c.operations[i],b),x)===a;break;
case "min":ok=Math.min(...c.args)===a;break;case "max":ok=Math.max(...c.args)===a;break;
case "two_expressions":ok=calc(c.expressions[0])===a.intermediate&&calc(c.expressions[1])===a.final;break;
case "truth":ok=a===((y==="×"?x*z===c.shown:x===z*c.shown.quotient+c.shown.remainder&&c.shown.remainder>=0&&c.shown.remainder<z)?"Đúng":"Sai");break;
case "division_truth":ok=a===((x===y*c.shown.quotient+c.shown.remainder&&c.shown.remainder>=0&&c.shown.remainder<y)?"Đúng":"Sai");break;
case "multiplication_truth":ok=a===((x*y===c.shown)?"Đúng":"Sai");break;
case "expression_truth":ok=a===((calc(c.expression)===c.shown)?"Đúng":"Sai");break;
case "match_expressions":ok=Object.keys(a).length===c.expressions.length&&c.expressions.every(e=>a[e.id]===calc(e.expression)&&v.options.includes(a[e.id]));break;
case "expression_extreme":ok=extreme(exprs(c),c.target,e=>calc(e.expression)).some(e=>e.id===a);break;
case "filter_expressions":ok=seteq(exprs(c).filter(e=>calc(e.expression)>c.target).map(e=>e.id),a);break;
case "digit_product":{let n=c.patterns.join("").split("?").length-1;let valid=combinations(n,c.digitOptions||[0,1,2,3,4,5,6,7,8,9],c.useEachOnce).filter(ds=>{let i=0,p=c.patterns.map(t=>t.replace(/\?/g,()=>ds[i++]));return p.every(s=>s.length===1||s[0]!=="0")&&+p[0]*+p[1]===+p[2]});ok=valid.length===1&&eq(valid[0],a.digits);break;}
case "operator_solutions":{let opts=combinations(c.template.split("?").length-1,c.operators),vals=opts.map(d=>calc(substitute(c.template,d))),t=c.goal==="max"?Math.max(...vals):c.goal==="min"?Math.min(...vals):c.target;ok=seteq(opts.filter((d,i)=>vals[i]===t),a.accepted);break;}
case "count_right_angles":ok=angles(c.points,c.segments).filter(t=>t.right).length===a;break;
case "count_nonright_at":ok=angles(c.points,c.segments).filter(t=>t.vertex===c.vertex&&!t.right).length===a;break;
case "midpoint":ok=eq(c.points[c.ends[0]].map((x,i)=>(x+c.points[c.ends[1]][i])/2),c.points[a]);break;
case "polygon_template":ok=eq(c.vertices,a.vertices)&&a.closed&&new Set(a.vertices.map(p=>p.join(","))).size===a.vertices.length;break;
case "circle_diameters":{let p=c.segments.filter(s=>s.includes(c.center)).map(s=>s.find(p=>p!==c.center));let d=[];for(let [i,u] of p.entries())for(let w of p.slice(i+1))if(Math.abs(c.angles[u]-c.angles[w])===180)d.push([u,w].sort().join(""));ok=seteq(d,a);break;}
case "circle_radii":ok=seteq(c.segments.filter(s=>s.includes(c.center)).map(s=>c.center+s.find(p=>p!==c.center)),a);break;
case "solid_cubes":ok=prod(c.dimensions)===a;break;case "stack_cubes":ok=prod(c.dimensions)+c.extra===a;break;
case "solid_count":ok=c.shapes.filter(s=>s===c.target).length===a;break;
case "painted_cube_count":{let [nx,ny,nz]=c.dimensions,n=0;for(let x=0;x<nx;x++)for(let y=0;y<ny;y++)for(let z=0;z<nz;z++)if([x===0,x===nx-1,y===0,y===ny-1,z===0,z===nz-1].filter(Boolean).length===c.paintedFaces)n++;ok=n===a;break;}
case "right_angle_vertices":case "angle_set":{let t=angles(c.points,c.segments).filter(t=>t.right===(c.op==="right_angle_vertices"?true:c.right)&&(!c.vertex||t.vertex===c.vertex));ok=seteq(t.map(t=>angleKey(t,c.points)),a.angles.map(t=>angleKey(t,c.points)));break;}
case "operation_word":ok=(a==="Gấp"?c.start*c.factor:c.start/c.factor)===c.end;break;
case "operation_label":ok=c.candidates.some(t=>t.label===a&&op(c.start,t.op,t.factor)===c.end);break;
case "length_difference":ok=y-x===a&&v.segment.endMm-v.segment.startMm===a;break;
case "extreme_length":ok=extreme(c.objects,c.target,t=>t.value*unit[t.unit]).some(t=>t.name===a);break;
case "sum_weights":ok=sum(c.weights)===a&&eq(c.weights,v.leftWeightsG);break;
case "read_scale":ok=c.valueG===a&&v.valueG===a&&a<=v.maxG;break;
case "read_cup":ok=c.cups.find(t=>t.id===c.cupId)?.valueMl===a;break;
case "lower_than_location":ok=c.items.some(t=>t.name===a&&t.value<c.items.find(t=>t.name===c.location).value);break;
case "extreme_named":ok=extreme(c.items,c.target,t=>t.value).some(t=>t.name===a);break;
case "temperature_words_to_number":ok=({"Ba mươi lăm độ xê":35,"Ba mươi mốt độ xê":31})[c.words]===a;break;
case "temperature_number_to_words":ok=c.valueC===15&&a==="Mười lăm độ xê";break;
case "middle":ok=[...c.args].sort((a,b)=>a-b)[1]===a;break;
case "sort_values":ok=eq([...c.args].sort((a,b)=>c.direction==="descending"?b-a:a-b),a);break;
case "single_weighing":ok=sum(a.selectedWeightsG)===sum(c.weightsG)&&a.targetG===c.targetG&&a.weighings===1&&a.keep===c.keep&&(a.keep==="remaining"?c.initialG-sum(a.selectedWeightsG):sum(a.selectedWeightsG))===c.targetG;break;
case "read_temperature":ok=c.valueC===a&&v.valueC===a&&a>=v.rangeC[0]&&a<=v.rangeC[1];break;
case "extreme_cup":ok=extreme(c.cups,c.target,t=>t.valueMl).some(t=>t.id===a);break;
case "cup_pair_sum":ok=a.length===2&&a[0]!==a[1]&&sum(a.map(id=>c.cups.find(t=>t.id===id).valueMl))===c.targetMl;break;
case "pour_sequence":ok=pour({capacitiesMl:c.capacitiesMl,actions:c.actions,targetMl:c.targetMl},a)&&pour(v,a);break;
case "unused_equal_product":{let t=combinations(4,c.numbers,true).filter(d=>d[0]*d[1]===d[2]*d[3]);let u=[...new Set(t.map(d=>c.numbers.find(n=>!d.includes(n))))];ok=u.length===1&&String(u[0])===a;break;}
case "equal_quotient_last":{let t=combinations(4,c.numbers,true).filter(d=>d[1]&&d[3]&&d[0]%d[1]===0&&d[2]%d[3]===0&&d[0]/d[1]===d[2]/d[3]);ok=[...new Set(t.map(d=>d[3]))].sort((a,b)=>a-b).join(" hoặc ")===a;break;}
case "number_fill":{let [u,w]=c.factors;let valid=[[u,w],[w,u]].map(d=>[...d,calc(substitute(c.template,d))]);ok=seteq([...new Map(valid.map(d=>[JSON.stringify(d),d])).values()],a.accepted);break;}
case "sort_expressions":ok=eq(c.expressions.map(calc).sort((a,b)=>c.direction==="ascending"?a-b:b-a),a);break;
case "balance_difference":ok=Math.abs(c.weights[0]-c.weights[1])===a.riceKg&&a.riceKg===c.target;break;
case "segments_midpoint":ok=seteq(c.options.filter(s=>eq(c.points[s[0]].map((x,i)=>(x+c.points[s[1]][i])/2),c.points[c.center])),a);break;
case "bounded_remainder":case "bounded_product":{let t=[];for(let i=c.min;i<=c.max;i++)if(c.op==="bounded_remainder"?i%c.divisor===c.remainder:i*c.multiplier<=c.limit)t.push(i);ok=(c.extreme==="max"?t.at(-1):t[0])===a;break;}
case "fill_remainder":ok=(y-x%y)%y===a;break;
case "weight_selection":ok=a.count===c.count&&a.selectedWeightsG.length===c.count&&sum(a.selectedWeightsG)===c.target&&hasWeights(c.available,a.selectedWeightsG);break;
default:assert(false,id,"unknown check "+c.op);
}
assert(ok,id,"check "+c.op);stats[c.op]=(stats[c.op]||0)+1;
}
const counts={SGK:all.filter(q=>q.book==="SGK").length,VBT:all.filter(q=>q.book==="VBT").length,BONUS:all.filter(q=>q.book==="BONUS").length};
assert(eq(counts,{SGK:437,VBT:484,BONUS:38})&&all.length===959,"all","counts");
assert(new Set(all.filter(q=>q.book==="SGK").map(q=>q.page)).size===47&&new Set(all.filter(q=>q.book==="VBT").map(q=>q.page)).size===50,"all","pages");
for(let l of data.lessons)assert(l.sgk.length>0&&l.vbt.length>0&&l.bonus.length===2&&l.chest.coins===100&&sum(l.bonus.map(q=>q.reward.coins))+l.chest.coins===300,"lesson "+l.lesson,"coverage/reward");
let explicit=all.filter(q=>q.check).length;
return {errors,counts,total:all.length,explicit,additional:numeric.length,manual:manual.length,open:open.length,examples:study.length,uncheckedExamples:all.filter(q=>q.kind==="study"&&!q.check).length,stats,manualIds:manual,additionalIds:numeric,normalizationChanges:data.changes.length};
}
const result=verify(prepare(readSource()));
console.log(JSON.stringify(result,null,2));
assert.equal(result.errors.length,0);
