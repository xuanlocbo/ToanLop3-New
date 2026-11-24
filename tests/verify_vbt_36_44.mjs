import fs from 'node:fs';
import assert from 'node:assert/strict';
function verify(docs,oldIds){
const ls=docs.flatMap(d=>d.lessons),all=ls.flatMap(l=>l.items),errors=[],stats={},manual=[],samples=[],seen=new Set(),legacy=new Set(),groups=new Set(),positions=new Map(all.map((q,i)=>[q.id,i]));
const ranges=[[88,89],[90,93],[94,97],[98,99],[100,101],[102,105],[106,108],[109,112],[113,115]];
function assert(ok,id,why){if(!ok)errors.push({id,why});}
const eq=(a,b)=>JSON.stringify(a)===JSON.stringify(b),seteq=(a,b)=>eq(a.map(x=>JSON.stringify(x)).sort(),b.map(x=>JSON.stringify(x)).sort());
function calc(e){let s=e.replace(/\s/g,"").replaceAll("×","*").replaceAll("−","-").replaceAll(":","/");if(!/^[0-9()+*\/-]+$/.test(s))throw Error(e);let out=[],ops=[],prec={"+":1,"-":1,"*":2,"/":2};for(let t of s.match(/\d+|[()+*\/-]/g)){if(/^\d+$/.test(t))out.push(+t);else if(t==="(")ops.push(t);else if(t===")"){while(ops.length&&ops.at(-1)!=="(")out.push(ops.pop());if(ops.pop()!=="(")throw Error(e);}else{while(ops.length&&prec[ops.at(-1)]>=prec[t])out.push(ops.pop());ops.push(t);}}while(ops.length)out.push(ops.pop());let st=[];for(let t of out){if(typeof t==="number")st.push(t);else{let b=st.pop(),a=st.pop();st.push(t==="+"?a+b:t==="-"?a-b:t==="*"?a*b:a/b);}}if(st.length!==1||!Number.isFinite(st[0]))throw Error(e);return st[0];}
function choices(n,opts,unique=false){let out=[];function rec(a){if(a.length===n){out.push(a);return;}for(let x of opts)if(!unique||!a.includes(x))rec([...a,x]);}rec([]);return out;}
function subst(t,values){let i=0;return t.replace(/\?/g,()=>values[i++]);}
function angles(points,segments){let adj=Object.fromEntries(Object.keys(points).map(x=>[x,[]]));for(let [a,b] of segments){adj[a].push(b);adj[b].push(a);}let out=[];for(let [v,neighbors] of Object.entries(adj)){let o=points[v],rays=[];for(let p of neighbors){let d=points[p].map((x,i)=>x-o[i]);let norm=Math.hypot(...d);let n=d.map(x=>x/norm);if(!rays.some(r=>r.n.every((x,i)=>Math.abs(x-n[i])<1e-9)))rays.push({p,n});}for(let i=0;i<rays.length;i++)for(let j=i+1;j<rays.length;j++){let a=rays[i],b=rays[j],dot=a.n[0]*b.n[0]+a.n[1]*b.n[1],cross=a.n[0]*b.n[1]-a.n[1]*b.n[0];if(Math.abs(cross)>1e-9)out.push({vertex:v,arms:[a.p,b.p].sort(),right:Math.abs(dot)<1e-9});}}return out;}
function canonicalAngle(t,points){let o=points[t.vertex],r=t.arms.map(p=>{let d=points[p].map((x,i)=>x-o[i]),n=Math.hypot(...d);return d.map(x=>(x/n).toFixed(7)).join(",");}).sort();return t.vertex+":"+r.join("|");}
for(let q of all){let id=q.id,a=q.answer,c=q.check,v=q.visual,r=ranges[q.lesson-36];assert(!seen.has(id),id,"duplicate ID");seen.add(id);assert(q.book==="VBT"&&r&&q.page>=r[0]&&q.page<=r[1]&&q.pdfPage===q.page+1&&id===`VBT.${q.lesson}.${q.period}.${q.page}.${q.exercise}.${q.part}`,id,"source ID");
for(let k of [id,q.legacyId,...(q.legacyIds||[])].filter(Boolean))legacy.add(k);assert(q.prompt&&q.solution&&q.hint,id,"required fields");groups.add(`${q.page}.${q.period}.${q.exercise}`);if(q.prerequisite)assert(positions.has(q.prerequisite)&&positions.get(q.prerequisite)<positions.get(id),id,"prerequisite order");if(q.kind==="study")samples.push(id);
if(["choice","multi","segment_multi","match_value"].includes(q.kind)){assert(Array.isArray(v?.options),id,"options");if(v?.options)assert(Array.isArray(a)?a.every(x=>v.options.includes(x)):v.options.includes(a),id,"option answer");}
if(v?.type==="long_division"){let rem=0,quot=0,started=false,ss=[];for(let ch of String(v.dividend)){let p=rem*10+Number(ch),d=Math.floor(p/v.divisor);rem=p%v.divisor;if(!started&&d===0)continue;started=true;quot=quot*10+d;ss.push({partialDividend:p,quotientDigit:d,product:d*v.divisor,remainder:rem});}assert(quot===a.quotient&&rem===a.remainder&&eq(ss,v.steps),id,"long division steps");stats.long_division_steps=(stats.long_division_steps||0)+1;}
if(v?.type==="long_multiplication"){let carry=0,ans=0,place=1,ss=[];for(let d of [...String(v.multiplicand)].map(Number).reverse()){let p=d*v.multiplier+carry;ss.push({digit:d,carryIn:carry,product:p,resultDigit:p%10,carryOut:Math.floor(p/10)});carry=Math.floor(p/10);ans+=(p%10)*place;place*=10;}assert(ans+carry*place===a&&eq(ss,v.steps),id,"long multiplication steps");stats.long_multiplication_steps=(stats.long_multiplication_steps||0)+1;}
if(!c){if(q.reviewMethod)manual.push(id);else assert(q.kind==="study"&&a===null,id,"missing check");continue;}let ok=false;let [x,y]=c.args||[];
switch(c.op){
case "expression":ok=calc(c.expression)===a;break;
case "divmod":ok=x===y*a.quotient+a.remainder&&a.remainder>=0&&a.remainder<y;break;
case "two_expressions":ok=calc(c.expressions[0])===a.intermediate&&calc(c.expressions[1])===a.final;break;
case "unused_equal_product":{let valid=choices(4,c.numbers,true).filter(d=>d[0]*d[1]===d[2]*d[3]);let unused=[...new Set(valid.map(d=>c.numbers.find(x=>!d.includes(x))))];ok=unused.length===1&&String(unused[0])===a;break;}
case "equal_quotient_last":{let valid=choices(4,c.numbers,true).filter(d=>d[1]&&d[3]&&d[0]%d[1]===0&&d[2]%d[3]===0&&d[0]/d[1]===d[2]/d[3]);ok=[...new Set(valid.map(d=>d[3]))].sort((x,y)=>x-y).join(" hoặc ")===a;break;}
case "division_truth":ok=a===((x===y*c.shown.quotient+c.shown.remainder&&c.shown.remainder>=0&&c.shown.remainder<y)?"Đúng":"Sai");break;
case "multiplication_truth":ok=a===((x*y===c.shown)?"Đúng":"Sai");break;
case "expression_truth":ok=a===((calc(c.expression)===c.shown)?"Đúng":"Sai");break;
case "expression_extreme":{let xs=c.expressions.map(calc),t=c.target==="max"?Math.max(...xs):Math.min(...xs);ok=xs[c.expressions.indexOf(a)]===t;break;}
case "number_fill":{let [u,w]=c.factors;let valid=[[u,w],[w,u]].map(d=>[...d,calc(subst(c.template,d))]);let distinct=[...new Map(valid.map(d=>[JSON.stringify(d),d])).values()];ok=seteq(distinct,a.accepted);break;}
case "sort_expressions":ok=eq(c.expressions.map(calc).sort((x,y)=>c.direction==="ascending"?x-y:y-x),a);break;
case "ceil_div":ok=Math.ceil(x/y)===a;break;case "remainder":ok=x%y===a;break;
case "digit_product":{let n=c.patterns.join("").split("?").length-1;let valid=choices(n,c.digitOptions||[0,1,2,3,4,5,6,7,8,9],c.useEachOnce).filter(ds=>{let i=0,p=c.patterns.map(p=>p.replace(/\?/g,()=>ds[i++]));return p.every(s=>s.length===1||s[0]!=="0")&&+p[0]*+p[1]===+p[2]});ok=valid.length===1&&eq(valid[0],a.digits);break;}
case "filter_expressions":ok=seteq(c.expressions.filter(e=>calc(e)>c.target),a);break;
case "operator_solutions":{let opts=choices(c.template.split("?").length-1,c.operators),vals=opts.map(d=>calc(subst(c.template,d))),t=c.goal==="max"?Math.max(...vals):c.goal==="min"?Math.min(...vals):c.target;ok=seteq(opts.filter((d,i)=>vals[i]===t),a.accepted);break;}
case "count_nonright_at":ok=angles(c.points,c.segments).filter(x=>x.vertex===c.vertex&&!x.right).length===a;break;
case "count_right_angles":ok=angles(c.points,c.segments).filter(x=>x.right).length===a;break;
case "midpoint":{let p=c.points[c.ends[0]],r=c.points[c.ends[1]];ok=eq(p.map((x,i)=>(x+r[i])/2),c.points[a]);break;}
case "polygon_template":ok=eq(c.vertices,a.vertices)&&a.closed===true&&new Set(a.vertices.map(x=>x.join(","))).size===a.vertices.length;break;
case "circle_diameters":{let points=c.segments.filter(s=>s.includes(c.center)).map(s=>s.find(p=>p!==c.center)),ds=[];for(let [i,p] of points.entries())for(let t of points.slice(i+1))if(Math.abs(c.angles[p]-c.angles[t])===180)ds.push([p,t].sort().join(""));ok=seteq(ds,a);break;}
case "circle_radii":ok=seteq(c.segments.filter(s=>s.includes(c.center)).map(s=>c.center+s.find(p=>p!==c.center)),a);break;
case "stack_cubes":ok=c.dimensions.reduce((s,d)=>s*d,1)+c.extra===a;break;
case "solid_count":ok=c.shapes.filter(x=>x===c.target).length===a;break;
case "painted_cube_count":{let [nx,ny,nz]=c.dimensions,n=0;for(let x=0;x<nx;x++)for(let y=0;y<ny;y++)for(let z=0;z<nz;z++)if([x===0,x===nx-1,y===0,y===ny-1,z===0,z===nz-1].filter(Boolean).length===c.paintedFaces)n++;ok=n===a;break;}
case "balance_difference":ok=Math.abs(c.weights[0]-c.weights[1])===c.target&&a.riceKg===c.target;break;
case "segments_midpoint":ok=seteq(c.options.filter(s=>eq(c.points[s[0]].map((x,i)=>(x+c.points[s[1]][i])/2),c.points[c.center])),a);break;
case "angle_set":{let expected=angles(c.points,c.segments).filter(t=>t.right===c.right&&(!c.vertex||t.vertex===c.vertex));ok=seteq(expected.map(t=>canonicalAngle(t,c.points)),a.angles.map(t=>canonicalAngle(t,c.points)));break;}
default:assert(false,id,"unknown check "+c.op);
}
assert(ok,id,"check "+c.op);stats[c.op]=(stats[c.op]||0)+1;}
assert(eq(ls.map(x=>x.lesson),[36,37,38,39,40,41,42,43,44]),"all","lesson range");assert(new Set(all.map(x=>x.page)).size===28,"all","page coverage");assert(oldIds.length===281&&oldIds.every(x=>legacy.has(x)),"all","legacy coverage");return {errors,stats,manual,samples,records:all.length,checked:all.filter(q=>q.check).length,counts:ls.map(l=>[l.lesson,l.items.length]),groups:groups.size,legacy:oldIds.length,uncovered:oldIds.filter(x=>!legacy.has(x)),pages:[...new Set(all.map(q=>q.page))]};
}
const base='app/src/main/assets/curriculum/';
const docs=["vbt_36_38.json","vbt_39_41.json","vbt_42_44.json"].map(n=>JSON.parse(fs.readFileSync(base+n,'utf8')));
const oldIds=[...fs.readFileSync('app/src/main/java/vn/mocnhien/toan3/BookBank.java','utf8').matchAll(/^\s*add\((3[6-9]|4[0-4]),(\d+),(\d+),(\d+),(\d+),Kind\./gm)].map(m=>'VBT.'+m.slice(1,6).join('.'));
const result=verify(docs,oldIds);
console.log(JSON.stringify(result,null,2));
assert.equal(result.errors.length,0);
