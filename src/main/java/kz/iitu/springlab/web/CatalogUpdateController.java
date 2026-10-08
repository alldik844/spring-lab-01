package kz.iitu.springlab.web;

import kz.iitu.springlab.service.CatalogUpdateService;
import kz.iitu.springlab.service.CatalogUpdateService.Change;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab4")
public class CatalogUpdateController {

    private final CatalogUpdateService service;

    public CatalogUpdateController(CatalogUpdateService service) {
        this.service = service;
    }

    @PutMapping("/item/{id}")
    public Change update(
            @PathVariable("id") long id,
            @RequestParam("name") String name) {
        return service.updateItem(id, name);
    }
}