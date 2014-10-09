package com.nx.controller.message;

/**
 * Created by Neal on 2014-10-08.
 */
import com.nx.domain.Message;
import com.nx.repositories.message.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/message")
public class MessageController {
    private MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @RequestMapping
    public ModelAndView list() {
        Iterable<Message> messages = messageRepository.findAll();
        return new ModelAndView("messages/inbox", "messages", messages);
    }

    @RequestMapping("{id}")
    public ModelAndView view(@PathVariable("id") Message message) {
        return new ModelAndView("messages/show", "message", message);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Message message, RedirectAttributes redirect) {
        messageRepository.delete(message);
        redirect.addFlashAttribute("globalMessage", "Message removed successfully");
        return "redirect:/";
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@ModelAttribute Message message) {
        return "messages/compose";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid Message message, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("messages/compose");
        }
        message = messageRepository.save(message);
        redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
        return new ModelAndView("redirect:/{message.id}", "message.id", message.getId());
    }
}

