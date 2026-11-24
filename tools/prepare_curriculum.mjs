import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
export const names=["sgk_26_29","sgk_30_35","sgk_36_37","sgk_38_40","sgk_41_42","sgk_43_44","vbt_26_29","vbt_30_35","vbt_36_38","vbt_39_41","vbt_42_44","bonus_26_44"];
export function prepare(docs) {
 const rows=docs.flatMap(d=>d.lessons.flatMap(l=>l.items.map(q=>JSON.parse(JSON.stringify(q))))); const by=new Map(rows.map(q=>[q.id,q]));const changes=[];
 function patch(id,fields,reason){let q=by.get(id);if(!q)throw Error("unknown patch "+id);Object.assign(q,fields);changes.push({id,reason});}
 let q=by.get("SGK.28.82.HD.1.a");
 patch(q.id,{answer:{intermediate:q.answer.step1.result,final:q.answer.final,unit:q.answer.unit},workedSteps:[q.answer.step1,q.answer.step2],check:{op:"two_expressions",expressions:["5×3","5+15"]}},"Thống nhất đáp án hai bước, giữ các ô điền phép tính theo nguồn.");
 for(let root of ["SGK.27.80.LT.1","SGK.29.84.LT2.2","SGK.35.95.LT2.1"])for(let letter of ["a","b","c"]){let a=by.get(root+"."+letter+"1"),b=by.get(root+"."+letter+"2");if(!a||!b)continue;patch(a.id,{visual:{...a.visual,activeStep:0}},"Mở đầu chuỗi phép tính.");patch(b.id,{visual:{...b.visual,activeStep:1},prerequisite:a.id},"Chỉ mở bước tiếp sau bước trước.");}
 for(let [a,b] of [["VBT.27.1.69.4.1","VBT.27.1.69.4.2"],["VBT.27.1.69.4.3","VBT.27.1.69.4.4"],["VBT.27.2.70.1.1","VBT.27.2.70.1.2"],["VBT.27.2.70.1.3","VBT.27.2.70.1.4"]])patch(b,{prerequisite:a},"Liên kết chuỗi còn thiếu.");
 patch("VBT.40.1.100.2.3",{prerequisites:["VBT.40.1.100.2.1","VBT.40.1.100.2.2"]},"Hai nhánh tính độc lập phải hoàn thành trước khi so sánh.");
 // A solved example is not a scored task. Preserve its checked answer separately.
 for(let r of rows){
  if(r.kind==="study"&&r.answer!==null){r.sampleAnswer=r.answer;r.answer=null;changes.push({id:r.id,reason:"Tách kết quả mẫu khỏi đáp án chấm điểm."});}
  if(r.kind==="practice_measure"){r.visual={...r.visual,requiresAdultReview:true};delete r.visual.referenceApproxMm;delete r.visual.referenceMethod;changes.push({id:r.id,reason:"Không chấm theo số đo ước lượng từ ảnh PDF."});}
  if(r.kind==="compose_story"){r.reviewRequired=true;r.visual={...r.visual,requiresAdultReview:true};}
  if(r.id==="SGK.35.96.GAME.1.rules"){r.visual={...r.visual,sourceRule:{...r.visual.sourceRule,groupPlay:true},appRule:{overshoot:"stay_at_turn_origin",wrongAnswer:"return_to_turn_origin",animal:"jump_then_solve_destination",finish:"exact_return_to_start_after_one_lap"},implementationNote:"Chơi nhóm là luật nguồn; ứng dụng hỗ trợ một người hoặc cùng bạn. Nếu gieo vượt đích, giữ vị trí chờ lượt sau là quy ước bổ sung cho ứng dụng."};changes.push({id:r.id,reason:"Tách luật nguồn với quy ước chơi số; không gán quy ước thêm là nguyên văn."});}
  if(r.kind==="balance_select"&&r.check?.op==="single_weighing"){r.legacyAnswer=r.answer;r.answer={targetG:r.check.targetG,selectedWeightsG:r.check.weightsG,initialG:r.check.initialG,keep:r.check.keep,weighings:1};changes.push({id:r.id,reason:"Chuẩn hóa đáp án cân theo thao tác, không chỉ chọn câu văn."});}
  r.prerequisites=[...new Set([...(r.prerequisites||[]),...(r.prerequisite?[r.prerequisite]:[])])];delete r.prerequisite;
  r.schemaVersion=2;
  let mode=r.kind==="study"?"example":r.kind.startsWith("practice")?"adult_review":r.kind==="compose_story"?"numeric_and_adult":r.kind==="board_game"?"game_state":["pour","balance_select","draw","angle_multi","point_select","route_match","operator_fill","number_fill","digit_fill","match","match_value","multi","segment_multi","order"].includes(r.kind)?"interaction":"auto";
  let comparison=({number:"integer",multiplication:"integer",division:"quotient_remainder",division_story:"quotient_remainder",steps:"intermediate_final",compose_story:"intermediate_final_and_adult",choice:"option_value",multi:"unordered_unique_values",segment_multi:"unordered_segments",match:"exact_mapping",match_value:"option_value",order:"ordered_values",digit_fill:"ordered_digits",operator_fill:r.answer?.requireAll?"all_accepted_sequences":"any_accepted_sequence",number_fill:"any_accepted_sequence",pour:"valid_pour_state",balance_select:"valid_balance_state",draw:"closed_polygon_equivalence",point_select:"point_label",angle_multi:"angle_ray_equivalence",route_match:"bridge_and_basket",ruler:"integer",scale:"integer",balance:"integer",cup:"integer",thermometer:"integer",board_game:"board_completion",study:"none",practice:"adult_confirmation",practice_measure:"adult_confirmation",practice_compose_story:"adult_confirmation"})[r.kind];if(!comparison)throw Error("unmapped kind "+r.kind);
  r.grading={mode,comparison,scoreEligible:!["example","adult_review"].includes(mode),reveal:"after_submit_or_explicit_solution",allowRetry:true};
  if(r.kind==="choice"&&r.visual?.requiresReason){r.grading.requiresReason=true;r.grading.comparison="option_value_with_reason";}
  if(r.kind==="steps"||r.kind==="compose_story"){if(!Number.isInteger(r.answer.intermediate)||!Number.isInteger(r.answer.final))throw Error("steps "+r.id);r.grading.requireAllSteps=true;}
  if(r.kind==="division"||r.kind==="division_story"){r.grading.requireQuotient=true;r.grading.requireRemainder=r.answer.remainder!==0;r.grading.defaultRemainder=0;}
  if(r.kind==="balance_select")r.grading.checkWeightMultiplicity=true;
  if(r.kind==="pour")r.grading.requireValidActions=true;
  if(r.kind==="draw")r.grading.rejectSelfCrossing=true;
  if(r.kind==="board_game")r.grading.requireCorrectLandingAnswers=true;
  if(r.book!=="BONUS")r.reward={coins:r.grading.scoreEligible?10:0,claimId:r.id,once:true};
  else r.reward={...r.reward,once:true};
  r.source={book:r.book,lesson:r.lesson,printedPage:r.page,pdfPage:r.pdfPage,section:r.section,exercise:r.exercise,part:r.part,additionalPrintedPages:r.sourcePages||[],original:r.book==="BONUS"};
  // Correct answer, solution, derivations and internal model fields must never be put into prompt views as raw JSON.
  r.presentation={renderByType:true,showHintOnRequest:true,showSolutionAfterSubmit:true,hideInternalFields:["answer","sampleAnswer","check","workedSteps","legacyAnswer","solutionActions","expectedAction","steps","targetAnswers","reason","z"]};
 }
 const lessons=[];for(let l=26;l<=44;l++){let raw=docs.find(d=>d.source.book==="SGK"&&d.lessons.some(x=>x.lesson===l)).lessons.find(x=>x.lesson===l);lessons.push({lesson:l,title:raw.title,sgk:rows.filter(q=>q.lesson===l&&q.book==="SGK"),vbt:rows.filter(q=>q.lesson===l&&q.book==="VBT"),bonus:rows.filter(q=>q.lesson===l&&q.book==="BONUS"),chest:{id:`CHEST.${l}`,requires:[`BONUS.${l}.1`,`BONUS.${l}.2`],coins:100,once:true}});}
 let ids=new Map(rows.map((q,i)=>[q.id,i]));if(ids.size!==rows.length||rows.length!==959)throw Error("coverage");
 for(let q of rows)for(let p of q.prerequisites)if(!ids.has(p)||ids.get(p)>=ids.get(q.id))throw Error("prerequisite "+q.id+" "+p);
 return {schemaVersion:2,sourceRecordCount:921,bonusCount:38,recordCount:rows.length,lessons,changes,rewardPolicy:{currency:"sao hồng",normal:10,challenge:80,expert:120,chest:100,maxBonusPerLesson:300,oneTimeByClaimId:true,wrongAnswerPenalty:0,allowReplay:true,allowHint:true,noRandomPaidRewards:true,persistClaimsAndBalanceAtomically:true}};
}
export function readSource(){return names.map(n=>JSON.parse(fs.readFileSync('app/src/main/assets/curriculum/'+n+'.json','utf8')));}
if(process.argv[1]&&path.resolve(process.argv[1])===fileURLToPath(import.meta.url)){const data=prepare(readSource());const out=process.argv[2]||'app/build/generated/curriculum/runtime_v2.json';fs.mkdirSync(path.dirname(out),{recursive:true});fs.writeFileSync(out,JSON.stringify(data));console.log('CURRICULUM_PREPARED '+data.recordCount+' '+out);}
