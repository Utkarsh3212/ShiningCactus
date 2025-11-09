package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.Rule.RuleDTO;
import com.chalk.ffs.Service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping("/flags/{flagId}/rules")
    public ResponseEntity<RuleDTO> createRule(@PathVariable Long flagId, @RequestBody RuleDTO ruleDTO) {
        RuleDTO createdRule = ruleService.createRule(flagId, ruleDTO);
        return ResponseEntity.status(201).body(createdRule);
    }

    @PutMapping("/rules/{ruleId}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable Long ruleId, @RequestBody RuleDTO ruleDTO) {
        RuleDTO updatedRule = ruleService.updateRule(ruleId, ruleDTO);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        ruleService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }
}
